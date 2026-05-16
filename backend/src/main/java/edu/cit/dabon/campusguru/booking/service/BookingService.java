package edu.cit.dabon.campusguru.booking.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import edu.cit.dabon.campusguru.booking.dto.BookingResponse;
import edu.cit.dabon.campusguru.booking.dto.CreateBookingRequest;
import edu.cit.dabon.campusguru.booking.dto.UpdateBookingStatusRequest;
import edu.cit.dabon.campusguru.booking.model.Booking;
import edu.cit.dabon.campusguru.booking.model.BookingStatus;
import edu.cit.dabon.campusguru.booking.repository.BookingRepository;
import edu.cit.dabon.campusguru.listing.model.Listing;
import edu.cit.dabon.campusguru.listing.repository.ListingRepository;
import edu.cit.dabon.campusguru.auth.model.User;
import edu.cit.dabon.campusguru.auth.service.AuthenticatedUserService;
import edu.cit.dabon.campusguru.googleauth.service.GoogleAuthService;
import edu.cit.dabon.campusguru.notification.service.NotificationsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ListingRepository listingRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificationsService notificationsService;
    private final GoogleAuthService googleAuthService;

    public BookingService(BookingRepository bookingRepository,
                          ListingRepository listingRepository,
                          AuthenticatedUserService authenticatedUserService,
                          NotificationsService notificationsService,
                          GoogleAuthService googleAuthService) {
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.notificationsService = notificationsService;
        this.googleAuthService = googleAuthService;
    }

    public BookingResponse createBooking(CreateBookingRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!"LEARNER".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only learners can create bookings");
        }

        validateCreateBookingRequest(request);

        Listing listing = listingRepository.findById(request.getListingId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        // Parse booked start/end if provided (from slot picker)
        LocalDateTime bookedStart = null;
        LocalDateTime bookedEnd = null;
        if (request.getBookedStart() != null && request.getBookedEnd() != null) {
            bookedStart = LocalDateTime.parse(request.getBookedStart());
            bookedEnd = LocalDateTime.parse(request.getBookedEnd());
        }

        Booking booking = Booking.builder()
            .listing(listing)
            .learner(currentUser)
            .requestedTime(request.getRequestedTime().trim())
            .paymentType(request.getPaymentType().trim())
            .status(BookingStatus.PENDING)
            .bookedStart(bookedStart)
            .bookedEnd(bookedEnd)
            .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Create a Google Calendar event on the tutor's calendar for the booked slot
        User tutor = listing.getTutor();
        if (tutor.isCalendarConnected() && bookedStart != null && bookedEnd != null) {
            try {
                Calendar calendarService = googleAuthService.getCalendarService(tutor.getUserId());
                Event event = new Event()
                    .setSummary("Tutoring session – " + listing.getSubject() + " with " + currentUser.getFirstName())
                    .setDescription("Booking request from " + currentUser.getFirstName() + " " + currentUser.getLastName()
                        + "\nPayment: " + request.getPaymentType()
                        + "\nStatus: PENDING");

                ZoneId zone = ZoneId.systemDefault();
                event.setStart(new EventDateTime()
                    .setDateTime(new DateTime(ZonedDateTime.of(bookedStart, zone).toInstant().toEpochMilli())));
                event.setEnd(new EventDateTime()
                    .setDateTime(new DateTime(ZonedDateTime.of(bookedEnd, zone).toInstant().toEpochMilli())));

                Event created = calendarService.events().insert("primary", event).execute();
                savedBooking.setNylasEventId(created.getId()); // Reusing existing column for Google event ID
                savedBooking = bookingRepository.save(savedBooking);
            } catch (IOException e) {
                // Calendar event creation failed — booking still succeeds
            }
        }

        // Send BOOKING_RECEIVED notification to the tutor
        String learnerName = currentUser.getFirstName() + " " + currentUser.getLastName();
        notificationsService.createNotification(
            tutor.getUserId(),
            "BOOKING_RECEIVED",
            savedBooking.getId(),
            learnerName + " requested a tutoring session for " + listing.getSubject()
                + " at " + request.getRequestedTime()
        );

        return toResponse(savedBooking);
    }

    public List<BookingResponse> getPendingBookingsForCurrentTutor() {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!"TUTOR".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only tutors can view pending bookings");
        }

        return bookingRepository.findByListingTutorUserIdAndStatusOrderByCreatedAtDesc(
                currentUser.getUserId(),
                BookingStatus.PENDING
            )
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public BookingResponse updateBookingStatus(Long bookingId, UpdateBookingStatusRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!"TUTOR".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only tutors can update booking statuses");
        }

        if (request.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }

        Booking booking = bookingRepository.findByIdAndListingTutorUserId(bookingId, currentUser.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found for current tutor"));

        if (request.getStatus() == BookingStatus.ACCEPTED) {
            if (isBlank(request.getMeetingLink())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "meeting_link is required when status is ACCEPTED");
            }
            booking.setMeetingLink(request.getMeetingLink().trim());
            booking.setDeclineReason(null);

            // Notify learner about acceptance
            User learner = booking.getLearner();
            notificationsService.createNotification(
                learner.getUserId(),
                "BOOKING_ACCEPTED",
                booking.getId(),
                "Your booking for " + booking.getListing().getSubject()
                    + " has been accepted! Meeting link: " + request.getMeetingLink().trim()
            );
        }

        if (request.getStatus() == BookingStatus.DECLINED) {
            if (isBlank(request.getDeclineReason())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "decline_reason is required when status is DECLINED");
            }
            booking.setDeclineReason(request.getDeclineReason().trim());
            booking.setMeetingLink(null);

            // Notify learner about decline
            User learner = booking.getLearner();
            notificationsService.createNotification(
                learner.getUserId(),
                "BOOKING_DECLINED",
                booking.getId(),
                "Your booking for " + booking.getListing().getSubject()
                    + " was declined. Reason: " + request.getDeclineReason().trim()
            );
        }

        booking.setStatus(request.getStatus());

        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }

    private BookingResponse toResponse(Booking booking) {
        User learner = booking.getLearner();
        String learnerName = learner.getFirstName() + " " + learner.getLastName();

        return BookingResponse.builder()
            .id(booking.getId())
            .listingId(booking.getListing().getId())
            .subject(booking.getListing().getSubject())
            .learnerId(learner.getUserId().toString())
            .learnerName(learnerName.trim())
            .requestedTime(booking.getRequestedTime())
            .paymentType(booking.getPaymentType())
            .status(booking.getStatus())
            .meetingLink(booking.getMeetingLink())
            .declineReason(booking.getDeclineReason())
            .build();
    }

    private void validateCreateBookingRequest(CreateBookingRequest request) {
        if (request.getListingId() == null || isBlank(request.getRequestedTime()) || isBlank(request.getPaymentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "listingId, requestedTime, and paymentType are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

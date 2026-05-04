package edu.cit.dabon.campusguru.bookings.service;

import edu.cit.dabon.campusguru.auth.entity.User;
import edu.cit.dabon.campusguru.auth.service.AuthenticatedUserService;
import edu.cit.dabon.campusguru.bookings.dto.BookingResponse;
import edu.cit.dabon.campusguru.bookings.dto.CreateBookingRequest;
import edu.cit.dabon.campusguru.bookings.dto.UpdateBookingStatusRequest;
import edu.cit.dabon.campusguru.bookings.entity.Booking;
import edu.cit.dabon.campusguru.bookings.enums.BookingStatus;
import edu.cit.dabon.campusguru.bookings.repository.BookingRepository;
import edu.cit.dabon.campusguru.listings.entity.Listing;
import edu.cit.dabon.campusguru.listings.repository.ListingRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ListingRepository listingRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public BookingService(BookingRepository bookingRepository,
                          ListingRepository listingRepository,
                          AuthenticatedUserService authenticatedUserService) {
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @SuppressWarnings("null")
    public BookingResponse createBooking(CreateBookingRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!"LEARNER".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only learners can create bookings");
        }

        validateCreateBookingRequest(request);

        Listing listing = listingRepository.findById(Objects.requireNonNull(request.getListingId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        Booking booking = Booking.builder()
                .listing(listing)
                .learner(currentUser)
                .requestedTime(request.getRequestedTime().trim())
                .paymentType(request.getPaymentType().trim())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(Objects.requireNonNull(booking));
        return toResponse(savedBooking);
    }

    public List<BookingResponse> getPendingBookingsForTutor() {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!"TUTOR".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only tutors can view pending bookings");
        }

        return bookingRepository.findByListingTutorUserIdAndStatusOrderByCreatedAtDesc(
                        currentUser.getUserId(),
                        BookingStatus.PENDING)
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
        }

        if (request.getStatus() == BookingStatus.DECLINED) {
            if (isBlank(request.getDeclineReason())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "decline_reason is required when status is DECLINED");
            }
            booking.setDeclineReason(request.getDeclineReason().trim());
            booking.setMeetingLink(null);
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

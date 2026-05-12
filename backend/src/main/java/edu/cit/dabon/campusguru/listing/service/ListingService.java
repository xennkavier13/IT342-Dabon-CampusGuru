package edu.cit.dabon.campusguru.listing.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import edu.cit.dabon.campusguru.listing.dto.AvailableSlot;
import edu.cit.dabon.campusguru.listing.dto.CreateListingRequest;
import edu.cit.dabon.campusguru.listing.dto.ListingResponse;
import edu.cit.dabon.campusguru.listing.dto.SetAvailabilityRequest;
import edu.cit.dabon.campusguru.listing.model.Listing;
import edu.cit.dabon.campusguru.auth.model.User;
import edu.cit.dabon.campusguru.listing.repository.ListingRepository;
import edu.cit.dabon.campusguru.auth.service.AuthenticatedUserService;
import edu.cit.dabon.campusguru.googleauth.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final GoogleAuthService googleAuthService;

    @Value("${scheduling.slot-duration-minutes:60}")
    private int slotDurationMinutes;

    public ListingService(ListingRepository listingRepository,
            AuthenticatedUserService authenticatedUserService,
            GoogleAuthService googleAuthService) {
        this.listingRepository = listingRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.googleAuthService = googleAuthService;
    }

    public ListingResponse createListing(CreateListingRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!"TUTOR".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only tutors can create listings");
        }

        validateListingRequest(request);

        Listing listing = Listing.builder()
                .tutor(currentUser)
                .subject(request.getSubject().trim())
                .contactInfo(request.getContactInfo().trim())
                .proofOfCompetenceUrl(request.getProofOfCompetenceUrl().trim())
                .build();

        Listing saved = listingRepository.save(listing);
        return toResponse(saved);
    }

    public List<ListingResponse> getRecentListings() {
        return listingRepository.findAll().stream()
                .sorted(Comparator.comparing(Listing::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
    }

    public ListingResponse getListingById(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));
        return toResponse(listing);
    }

    public ListingResponse setAvailability(Long listingId, SetAvailabilityRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!"TUTOR".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only tutors can set availability");
        }

        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (!listing.getTutor().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own listings");
        }

        validateAvailabilityRequest(request);

        listing.setAvailabilityStartDate(request.getAvailabilityStartDate());
        listing.setAvailabilityEndDate(request.getAvailabilityEndDate());
        listing.setAvailabilityDailyStartTime(request.getAvailabilityDailyStartTime());
        listing.setAvailabilityDailyEndTime(request.getAvailabilityDailyEndTime());

        if (currentUser.isCalendarConnected()) {
            try {
                Calendar calendarService = googleAuthService.getCalendarService(currentUser.getUserId());

                if (listing.getGoogleCalendarEventId() != null) {
                    try {
                        calendarService.events().delete("primary", listing.getGoogleCalendarEventId()).execute();
                    } catch (IOException ignored) {}
                }

                Event event = new Event()
                    .setSummary("Available for tutoring – " + listing.getSubject())
                    .setDescription("CampusGuru availability block. Daily window: "
                        + request.getAvailabilityDailyStartTime() + " – " + request.getAvailabilityDailyEndTime())
                    .setTransparency("transparent");

                event.setStart(new EventDateTime().setDate(new DateTime(request.getAvailabilityStartDate().toString())));
                event.setEnd(new EventDateTime().setDate(new DateTime(request.getAvailabilityEndDate().plusDays(1).toString())));

                Event created = calendarService.events().insert("primary", event).execute();
                listing.setGoogleCalendarEventId(created.getId());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create Google Calendar event: " + e.getMessage());
            }
        }

        Listing saved = listingRepository.save(listing);
        return toResponse(saved);
    }

    public List<AvailableSlot> getAvailableSlots(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        if (listing.getAvailabilityStartDate() == null || listing.getAvailabilityEndDate() == null
            || listing.getAvailabilityDailyStartTime() == null || listing.getAvailabilityDailyEndTime() == null) {
            return Collections.emptyList();
        }

        List<AvailableSlot> candidateSlots = generateCandidateSlots(listing);

        if (candidateSlots.isEmpty()) {
            return candidateSlots;
        }

        User tutor = listing.getTutor();
        if (tutor.isCalendarConnected()) {
            try {
                Calendar calendarService = googleAuthService.getCalendarService(tutor.getUserId());
                List<TimePeriod> busyPeriods = fetchBusyPeriods(calendarService, listing);
                return filterAvailableSlots(candidateSlots, busyPeriods);
            } catch (Exception e) {
                return candidateSlots;
            }
        }

        return candidateSlots;
    }

    private List<AvailableSlot> generateCandidateSlots(Listing listing) {
        List<AvailableSlot> slots = new ArrayList<>();
        LocalDate startDate = listing.getAvailabilityStartDate();
        LocalDate endDate = listing.getAvailabilityEndDate();
        LocalTime dailyStart = listing.getAvailabilityDailyStartTime();
        LocalTime dailyEnd = listing.getAvailabilityDailyEndTime();
        LocalDate today = LocalDate.now();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.isBefore(today)) continue;

            LocalTime slotStart = dailyStart;
            while (slotStart.plusMinutes(slotDurationMinutes).compareTo(dailyEnd) <= 0) {
                LocalTime slotEnd = slotStart.plusMinutes(slotDurationMinutes);
                LocalDateTime start = LocalDateTime.of(date, slotStart);
                LocalDateTime end = LocalDateTime.of(date, slotEnd);

                if (start.isAfter(LocalDateTime.now())) {
                    slots.add(AvailableSlot.builder().start(start).end(end).build());
                }

                slotStart = slotEnd;
            }
        }

        return slots;
    }

    private List<TimePeriod> fetchBusyPeriods(Calendar calendarService, Listing listing) throws IOException {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime windowStart = listing.getAvailabilityStartDate()
            .atTime(listing.getAvailabilityDailyStartTime()).atZone(zone);
        ZonedDateTime windowEnd = listing.getAvailabilityEndDate()
            .atTime(listing.getAvailabilityDailyEndTime()).atZone(zone);

        FreeBusyRequest request = new FreeBusyRequest()
            .setTimeMin(new DateTime(windowStart.toInstant().toEpochMilli()))
            .setTimeMax(new DateTime(windowEnd.toInstant().toEpochMilli()))
            .setItems(List.of(new FreeBusyRequestItem().setId("primary")));

        FreeBusyResponse response = calendarService.freebusy().query(request).execute();
        var calendarBusy = response.getCalendars().get("primary");
        return calendarBusy != null && calendarBusy.getBusy() != null
            ? calendarBusy.getBusy()
            : Collections.emptyList();
    }

    private List<AvailableSlot> filterAvailableSlots(List<AvailableSlot> candidates, List<TimePeriod> busyPeriods) {
        if (busyPeriods.isEmpty()) return candidates;

        ZoneId zone = ZoneId.systemDefault();
        return candidates.stream()
            .filter(slot -> {
                long slotStartMs = slot.getStart().atZone(zone).toInstant().toEpochMilli();
                long slotEndMs = slot.getEnd().atZone(zone).toInstant().toEpochMilli();

                return busyPeriods.stream().noneMatch(busy -> {
                    long busyStart = busy.getStart().getValue();
                    long busyEnd = busy.getEnd().getValue();
                    return slotStartMs < busyEnd && slotEndMs > busyStart;
                });
            })
            .toList();
    }

    private ListingResponse toResponse(Listing listing) {
        String tutorName = listing.getTutor().getFirstName() + " " + listing.getTutor().getLastName();
        return ListingResponse.builder()
                .id(listing.getId())
                .tutorId(listing.getTutor().getUserId().toString())
                .tutorName(tutorName.trim())
                .subject(listing.getSubject())
                .contactInfo(listing.getContactInfo())
                .proofOfCompetenceUrl(listing.getProofOfCompetenceUrl())
                .availabilityStartDate(listing.getAvailabilityStartDate())
                .availabilityEndDate(listing.getAvailabilityEndDate())
                .availabilityDailyStartTime(listing.getAvailabilityDailyStartTime())
                .availabilityDailyEndTime(listing.getAvailabilityDailyEndTime())
                .calendarConnected(listing.getTutor().isCalendarConnected())
                .build();
    }

    private void validateListingRequest(CreateListingRequest request) {
        if (isBlank(request.getSubject()) || isBlank(request.getContactInfo())
                || isBlank(request.getProofOfCompetenceUrl())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "subject, contactInfo, and proofOfCompetenceUrl are required");
        }
    }

    private void validateAvailabilityRequest(SetAvailabilityRequest request) {
        if (request.getAvailabilityStartDate() == null || request.getAvailabilityEndDate() == null
            || request.getAvailabilityDailyStartTime() == null || request.getAvailabilityDailyEndTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All availability fields are required");
        }
        if (request.getAvailabilityEndDate().isBefore(request.getAvailabilityStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }
        if (!request.getAvailabilityDailyEndTime().isAfter(request.getAvailabilityDailyStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Daily end time must be after start time");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

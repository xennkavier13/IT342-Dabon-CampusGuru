package edu.cit.dabon.campusguru.listings.service;

import edu.cit.dabon.campusguru.auth.entity.User;
import edu.cit.dabon.campusguru.auth.service.AuthenticatedUserService;
import edu.cit.dabon.campusguru.listings.dto.CreateListingRequest;
import edu.cit.dabon.campusguru.listings.dto.ListingResponse;
import edu.cit.dabon.campusguru.listings.entity.Listing;
import edu.cit.dabon.campusguru.listings.repository.ListingRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ListingService(ListingRepository listingRepository,
                          AuthenticatedUserService authenticatedUserService) {
        this.listingRepository = listingRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @SuppressWarnings("null")
    public ListingResponse createListing(CreateListingRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        if (!"TUTOR".equalsIgnoreCase(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only tutors can create listings");
        }

        validateListingRequest(request);

        Listing listing = Listing.builder()
                .tutor(currentUser)
                .subject(request.getSubject().trim())
                .availableTime(request.getAvailableTime().trim())
                .contactInfo(request.getContactInfo().trim())
                .proofOfCompetenceUrl(request.getProofOfCompetenceUrl().trim())
                .build();

        Listing saved = listingRepository.save(Objects.requireNonNull(listing));
        return toResponse(saved);
    }

    public List<ListingResponse> getAllListings() {
        return listingRepository.findAll().stream()
                .sorted(Comparator.comparing(Listing::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
    }

    @SuppressWarnings("null")
    public ListingResponse getListingById(Long id) {
        Listing listing = listingRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));
        return toResponse(listing);
    }

    private ListingResponse toResponse(Listing listing) {
        String tutorName = listing.getTutor().getFirstName() + " " + listing.getTutor().getLastName();
        return ListingResponse.builder()
                .id(listing.getId())
                .tutorId(listing.getTutor().getUserId().toString())
                .tutorName(tutorName.trim())
                .subject(listing.getSubject())
                .availableTime(listing.getAvailableTime())
                .contactInfo(listing.getContactInfo())
                .proofOfCompetenceUrl(listing.getProofOfCompetenceUrl())
                .build();
    }

    private void validateListingRequest(CreateListingRequest request) {
        if (isBlank(request.getSubject()) || isBlank(request.getAvailableTime()) || isBlank(request.getContactInfo()) || isBlank(request.getProofOfCompetenceUrl())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "subject, availableTime, contactInfo, and proofOfCompetenceUrl are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

package edu.cit.dabon.campusguru.listing.controller;

import edu.cit.dabon.campusguru.listing.dto.AvailableSlot;
import edu.cit.dabon.campusguru.listing.dto.CreateListingRequest;
import edu.cit.dabon.campusguru.listing.dto.ListingResponse;
import edu.cit.dabon.campusguru.listing.dto.SetAvailabilityRequest;
import edu.cit.dabon.campusguru.listing.service.ListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "*")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping
    public ResponseEntity<ListingResponse> createListing(@RequestBody CreateListingRequest request) {
        ListingResponse created = listingService.createListing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<ListingResponse>> getAllListings() {
        return ResponseEntity.ok(listingService.getRecentListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getListingById(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    /**
     * Sets the availability window on a listing and creates a Google Calendar background event.
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<ListingResponse> setAvailability(@PathVariable Long id,
                                                            @RequestBody SetAvailabilityRequest request) {
        return ResponseEntity.ok(listingService.setAvailability(id, request));
    }

    /**
     * Returns available booking slots computed from the availability window
     * minus the tutor's busy calendar events.
     */
    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<AvailableSlot>> getAvailableSlots(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getAvailableSlots(id));
    }
}

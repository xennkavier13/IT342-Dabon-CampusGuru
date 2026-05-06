package edu.cit.dabon.campusguru.listing.repository;

import edu.cit.dabon.campusguru.listing.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}

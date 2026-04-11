package edu.cit.dabon.campusguru.repository;

import edu.cit.dabon.campusguru.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}

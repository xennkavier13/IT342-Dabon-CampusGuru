package edu.cit.dabon.campusguru.listings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.dabon.campusguru.listings.entity.Listing;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}

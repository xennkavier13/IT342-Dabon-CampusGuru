package edu.cit.dabon.campusguru.bookings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.dabon.campusguru.bookings.entity.Booking;
import edu.cit.dabon.campusguru.bookings.enums.BookingStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByListingTutorUserIdAndStatusOrderByCreatedAtDesc(UUID tutorId, BookingStatus status);
    Optional<Booking> findByIdAndListingTutorUserId(Long id, UUID tutorId);
}

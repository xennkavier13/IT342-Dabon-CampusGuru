package edu.cit.dabon.campusguru.booking.repository;

import edu.cit.dabon.campusguru.booking.model.Booking;
import edu.cit.dabon.campusguru.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByListingTutorUserIdAndStatusOrderByCreatedAtDesc(UUID tutorId, BookingStatus status);
    Optional<Booking> findByIdAndListingTutorUserId(Long id, UUID tutorId);
}

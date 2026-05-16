package edu.cit.dabon.campusguru.notification.repository;

import edu.cit.dabon.campusguru.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationsRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserUserIdOrderByCreatedAtDesc(UUID userId);
    long countByUserUserIdAndIsReadFalse(UUID userId);
}

package edu.cit.dabon.campusguru.notification.service;

import edu.cit.dabon.campusguru.auth.model.User;
import edu.cit.dabon.campusguru.auth.repository.UserRepository;
import edu.cit.dabon.campusguru.notification.model.Notification;
import edu.cit.dabon.campusguru.notification.repository.NotificationsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;
    private final UserRepository userRepository;

    public NotificationsService(NotificationsRepository notificationsRepository,
                                 UserRepository userRepository) {
        this.notificationsRepository = notificationsRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a notification for the given user. Called internally by BookingService.
     */
    public Notification createNotification(UUID userId, String type, Long bookingId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .bookingId(bookingId)
                .message(message)
                .isRead(false)
                .build();

        return notificationsRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationsRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(UUID notificationId) {
        Notification notification = notificationsRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setRead(true);
        notificationsRepository.save(notification);
    }

    public long getUnreadCount(UUID userId) {
        return notificationsRepository.countByUserUserIdAndIsReadFalse(userId);
    }
}

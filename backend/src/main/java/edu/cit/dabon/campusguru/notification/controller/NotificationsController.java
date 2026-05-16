package edu.cit.dabon.campusguru.notification.controller;

import edu.cit.dabon.campusguru.auth.model.User;
import edu.cit.dabon.campusguru.auth.service.AuthenticatedUserService;
import edu.cit.dabon.campusguru.notification.model.Notification;
import edu.cit.dabon.campusguru.notification.service.NotificationsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationsController {

    private final NotificationsService notificationsService;
    private final AuthenticatedUserService authenticatedUserService;

    public NotificationsController(NotificationsService notificationsService,
                                    AuthenticatedUserService authenticatedUserService) {
        this.notificationsService = notificationsService;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Returns all notifications for the authenticated user, sorted by created_at DESC.
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        User currentUser = authenticatedUserService.getCurrentUser();
        List<Notification> notifications = notificationsService.getNotificationsForUser(currentUser.getUserId());
        List<NotificationResponse> response = notifications.stream()
                .map(NotificationResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Marks a single notification as read.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        notificationsService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Returns the count of unread notifications for the authenticated user.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        User currentUser = authenticatedUserService.getCurrentUser();
        long count = notificationsService.getUnreadCount(currentUser.getUserId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Inline response DTO
    record NotificationResponse(
            String id,
            String type,
            Long bookingId,
            String message,
            boolean isRead,
            String createdAt
    ) {
        static NotificationResponse from(Notification n) {
            return new NotificationResponse(
                    n.getId().toString(),
                    n.getType(),
                    n.getBookingId(),
                    n.getMessage(),
                    n.isRead(),
                    n.getCreatedAt() != null ? n.getCreatedAt().toString() : null
            );
        }
    }
}

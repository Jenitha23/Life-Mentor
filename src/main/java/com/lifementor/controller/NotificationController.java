package com.lifementor.controller;

import com.lifementor.dto.response.ApiResponse;
import com.lifementor.dto.response.NotificationResponse;
import com.lifementor.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getNotifications(@RequestAttribute("userId") UUID userId) {
        try {
            List<NotificationResponse> notifications = notificationService.getNotifications(userId);
            return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
        } catch (Exception e) {
            log.error("Failed to retrieve notifications: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse> getUnreadCount(@RequestAttribute("userId") UUID userId) {
        try {
            long unreadCount = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(ApiResponse.success("Unread notification count retrieved successfully",
                    Map.of("unreadCount", unreadCount)));
        } catch (Exception e) {
            log.error("Failed to retrieve unread notification count: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse> markAsRead(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID notificationId) {
        try {
            notificationService.markAsRead(userId, notificationId);
            return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
        } catch (Exception e) {
            log.error("Failed to mark notification as read: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse> markAllAsRead(@RequestAttribute("userId") UUID userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
        } catch (Exception e) {
            log.error("Failed to mark all notifications as read: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}

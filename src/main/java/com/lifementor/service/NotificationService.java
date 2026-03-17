package com.lifementor.service;

import com.lifementor.dto.response.NotificationResponse;
import com.lifementor.entity.User;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void createNotification(User user, String type, String title, String message, String actionUrl);

    List<NotificationResponse> getNotifications(UUID userId);

    long getUnreadCount(UUID userId);

    void markAsRead(UUID userId, UUID notificationId);

    void markAllAsRead(UUID userId);
}

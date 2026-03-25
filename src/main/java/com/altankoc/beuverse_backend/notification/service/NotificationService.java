package com.altankoc.beuverse_backend.notification.service;

import com.altankoc.beuverse_backend.enums.NotificationType;
import com.altankoc.beuverse_backend.notification.dto.NotificationResponseDTO;
import org.springframework.data.domain.Page;

public interface NotificationService {

    void createNotification(Long studentId, Long senderStudentId, NotificationType type, Long postId, Long commentId);
    Page<NotificationResponseDTO> getNotifications(Long studentId, int page, int size);
    long getUnreadCount(Long studentId);
    void markAsRead(Long notificationId, Long studentId);
    void markAllAsRead(Long studentId);
}
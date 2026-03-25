package com.altankoc.beuverse_backend.notification.dto;

import com.altankoc.beuverse_backend.enums.NotificationType;
import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
        Long id,
        NotificationType type,
        StudentSummaryDTO sender,
        Long postId,
        Long commentId,
        boolean read,
        LocalDateTime createdAt
) {
}
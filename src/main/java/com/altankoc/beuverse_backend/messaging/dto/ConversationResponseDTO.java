package com.altankoc.beuverse_backend.messaging.dto;

import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;

import java.time.LocalDateTime;

public record ConversationResponseDTO(
        Long id,
        StudentSummaryDTO otherStudent,
        boolean accepted,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        long unreadCount,
        boolean requester
) {
}
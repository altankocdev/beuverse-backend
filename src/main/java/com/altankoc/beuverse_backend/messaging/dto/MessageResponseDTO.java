package com.altankoc.beuverse_backend.messaging.dto;

import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;

import java.time.LocalDateTime;

public record MessageResponseDTO(
        Long id,
        String content,
        StudentSummaryDTO sender,
        boolean read,
        LocalDateTime createdAt
) {
}
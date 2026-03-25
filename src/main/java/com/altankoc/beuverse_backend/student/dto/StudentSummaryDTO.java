package com.altankoc.beuverse_backend.student.dto;

public record StudentSummaryDTO(
        Long id,
        String firstName,
        String lastName,
        String username,
        String profilePhotoUrl
) {
}
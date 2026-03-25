package com.altankoc.beuverse_backend.student.dto;


import java.time.LocalDateTime;

public record StudentResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email,
        String bio,
        String profilePhotoUrl,
        String department,
        LocalDateTime createdAt
) {
}

package com.altankoc.beuverse_backend.auth.dto;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        StudentInfoDTO student
) {
    public record StudentInfoDTO(
            Long id,
            String username,
            String email,
            String role
    ) {
    }
}
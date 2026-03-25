package com.altankoc.beuverse_backend.like.dto;

public record LikeResponseDTO(
        Long id,
        Long postId,
        Long commentId,
        Long studentId,
        boolean liked
) {
}
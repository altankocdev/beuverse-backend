package com.altankoc.beuverse_backend.like.dto;

import jakarta.validation.constraints.NotNull;

public record LikeRequestDTO(

        Long postId,
        Long commentId
) {
}
package com.altankoc.beuverse_backend.post.dto;

import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDTO(
        Long id,
        String content,
        String tag,
        int likeCount,
        int commentCount,
        List<String> imageUrls,
        StudentSummaryDTO student,
        boolean isLiked,
        LocalDateTime createdAt
) {
}
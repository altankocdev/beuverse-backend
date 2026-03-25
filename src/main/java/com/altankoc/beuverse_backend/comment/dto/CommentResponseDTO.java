package com.altankoc.beuverse_backend.comment.dto;

import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        String content,
        int likeCount,
        int replyCount,
        StudentSummaryDTO student,
        Long parentCommentId,
        Long postId,
        String postOwnerUsername,
        boolean isLiked,
        LocalDateTime createdAt
) {
}
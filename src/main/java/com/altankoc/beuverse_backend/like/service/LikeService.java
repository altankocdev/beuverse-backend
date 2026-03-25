package com.altankoc.beuverse_backend.like.service;

import com.altankoc.beuverse_backend.like.dto.LikeResponseDTO;
import com.altankoc.beuverse_backend.post.dto.PostResponseDTO;
import org.springframework.data.domain.Page;

public interface LikeService {

    LikeResponseDTO togglePostLike(Long studentId, Long postId);
    LikeResponseDTO toggleCommentLike(Long studentId, Long commentId);
    boolean isPostLiked(Long studentId, Long postId);
    boolean isCommentLiked(Long studentId, Long commentId);
    Page<PostResponseDTO> getLikedPostsByStudentId(Long studentId, int page, int size);
}
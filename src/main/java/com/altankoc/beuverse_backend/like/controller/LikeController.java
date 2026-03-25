package com.altankoc.beuverse_backend.like.controller;

import com.altankoc.beuverse_backend.core.security.SecurityUtils;
import com.altankoc.beuverse_backend.like.dto.LikeResponseDTO;
import com.altankoc.beuverse_backend.like.service.LikeService;
import com.altankoc.beuverse_backend.post.dto.PostResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<LikeResponseDTO> togglePostLike(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.togglePostLike(SecurityUtils.getCurrentStudentId(), postId));
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<LikeResponseDTO> toggleCommentLike(@PathVariable Long commentId) {
        return ResponseEntity.ok(likeService.toggleCommentLike(SecurityUtils.getCurrentStudentId(), commentId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Boolean> isPostLiked(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.isPostLiked(SecurityUtils.getCurrentStudentId(), postId));
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<Boolean> isCommentLiked(@PathVariable Long commentId) {
        return ResponseEntity.ok(likeService.isCommentLiked(SecurityUtils.getCurrentStudentId(), commentId));
    }

    @GetMapping("/student/{studentId}/posts")
    public ResponseEntity<Page<PostResponseDTO>> getLikedPostsByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(likeService.getLikedPostsByStudentId(studentId, page, size));
    }
}
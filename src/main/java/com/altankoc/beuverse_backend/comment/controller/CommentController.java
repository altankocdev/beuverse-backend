package com.altankoc.beuverse_backend.comment.controller;

import com.altankoc.beuverse_backend.comment.dto.CommentRequestDTO;
import com.altankoc.beuverse_backend.comment.dto.CommentResponseDTO;
import com.altankoc.beuverse_backend.comment.service.CommentService;
import com.altankoc.beuverse_backend.core.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CommentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(SecurityUtils.getCurrentStudentId(), dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(
                postId, SecurityUtils.getCurrentStudentId(), page, size));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponseDTO>> getRepliesByCommentId(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getRepliesByCommentId(commentId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsByStudentId(studentId, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDTO dto) {
        return ResponseEntity.ok(commentService.updateComment(id, SecurityUtils.getCurrentStudentId(), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id, SecurityUtils.getCurrentStudentId());
        return ResponseEntity.noContent().build();
    }
}
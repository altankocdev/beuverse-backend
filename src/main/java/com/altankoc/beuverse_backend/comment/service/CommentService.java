package com.altankoc.beuverse_backend.comment.service;

import com.altankoc.beuverse_backend.comment.dto.CommentRequestDTO;
import com.altankoc.beuverse_backend.comment.dto.CommentResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {

    CommentResponseDTO createComment(Long studentId, CommentRequestDTO dto);
    CommentResponseDTO getCommentById(Long id);
    Page<CommentResponseDTO> getCommentsByPostId(Long postId, Long currentStudentId, int page, int size);
    List<CommentResponseDTO> getRepliesByCommentId(Long commentId);
    Page<CommentResponseDTO> getCommentsByStudentId(Long studentId, int page, int size);
    CommentResponseDTO updateComment(Long id, Long studentId, CommentRequestDTO dto);
    void deleteComment(Long id, Long studentId);
}
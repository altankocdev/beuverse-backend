package com.altankoc.beuverse_backend.comment.service;

import com.altankoc.beuverse_backend.comment.dto.CommentRequestDTO;
import com.altankoc.beuverse_backend.comment.dto.CommentResponseDTO;
import com.altankoc.beuverse_backend.comment.entity.Comment;
import com.altankoc.beuverse_backend.comment.mapper.CommentMapper;
import com.altankoc.beuverse_backend.comment.repository.CommentRepository;
import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.enums.NotificationType;
import com.altankoc.beuverse_backend.like.repository.LikeRepository;
import com.altankoc.beuverse_backend.notification.service.NotificationService;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.post.repository.PostRepository;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final StudentRepository studentRepository;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;
    private final LikeRepository likeRepository;


    @Override
    @Transactional
    public CommentResponseDTO createComment(Long studentId, CommentRequestDTO dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));

        Post post = postRepository.findById(dto.postId())
                .orElseThrow(() -> new ResourceNotFoundException("Post bulunamadı!"));

        Comment comment = commentMapper.toEntity(dto);
        comment.setStudent(student);
        comment.setPost(post);

        if (dto.parentCommentId() != null) {
            Comment parentComment = commentRepository.findById(dto.parentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Üst yorum bulunamadı!"));
            comment.setParentComment(parentComment);
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            commentRepository.save(parentComment);

            notificationService.createNotification(
                    parentComment.getStudent().getId(),
                    studentId,
                    NotificationType.REPLY,
                    dto.postId(),
                    parentComment.getId()
            );
        } else {
            notificationService.createNotification(
                    post.getStudent().getId(),
                    studentId,
                    NotificationType.COMMENT,
                    dto.postId(),
                    null
            );
        }

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return commentMapper.toResponseDTO(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum bulunamadı!"));
        return commentMapper.toResponseDTO(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getCommentsByPostId(Long postId, Long currentStudentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(comment -> {
                    CommentResponseDTO dto = commentMapper.toResponseDTO(comment);
                    boolean isLiked = likeRepository.existsByStudentIdAndCommentId(currentStudentId, comment.getId());
                    return new CommentResponseDTO(
                            dto.id(), dto.content(), dto.likeCount(), dto.replyCount(),
                            dto.student(), dto.parentCommentId(), dto.postId(),
                            dto.postOwnerUsername(), isLiked, dto.createdAt()
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getRepliesByCommentId(Long commentId) {
        return commentRepository.findByParentCommentId(commentId)
                .stream()
                .map(commentMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getCommentsByStudentId(Long studentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByStudentIdOrderByCreatedAtDesc(studentId, pageable)
                .map(commentMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public CommentResponseDTO updateComment(Long id, Long studentId, CommentRequestDTO dto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum bulunamadı!"));

        if (!comment.getStudent().getId().equals(studentId)) {
            throw new BusinessException("Bu yorumu düzenleme yetkiniz yok!");
        }

        comment.setContent(dto.content());
        return commentMapper.toResponseDTO(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long studentId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum bulunamadı!"));

        if (!comment.getStudent().getId().equals(studentId)) {
            throw new BusinessException("Bu yorumu silme yetkiniz yok!");
        }

        if (comment.getParentComment() != null) {
            Comment parentComment = comment.getParentComment();
            parentComment.setReplyCount(parentComment.getReplyCount() - 1);
            commentRepository.save(parentComment);
        }

        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);
        postRepository.save(post);

        commentRepository.delete(comment);
    }
}
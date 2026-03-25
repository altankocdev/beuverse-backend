package com.altankoc.beuverse_backend.like.service;

import com.altankoc.beuverse_backend.comment.entity.Comment;
import com.altankoc.beuverse_backend.comment.repository.CommentRepository;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.enums.NotificationType;
import com.altankoc.beuverse_backend.like.dto.LikeResponseDTO;
import com.altankoc.beuverse_backend.like.entity.Like;
import com.altankoc.beuverse_backend.like.mapper.LikeMapper;
import com.altankoc.beuverse_backend.like.repository.LikeRepository;
import com.altankoc.beuverse_backend.notification.service.NotificationService;
import com.altankoc.beuverse_backend.post.dto.PostResponseDTO;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.post.mapper.PostMapper;
import com.altankoc.beuverse_backend.post.repository.PostRepository;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final StudentRepository studentRepository;
    private final LikeMapper likeMapper;
    private final PostMapper postMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public LikeResponseDTO togglePostLike(Long studentId, Long postId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post bulunamadı!"));

        Optional<Like> existing = likeRepository.findByStudentIdAndPostId(studentId, postId);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            return new LikeResponseDTO(null, postId, null, studentId, false);
        } else {
            Like like = Like.builder()
                    .student(student)
                    .post(post)
                    .build();
            Like saved = likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);

            notificationService.createNotification(
                    post.getStudent().getId(),
                    studentId,
                    NotificationType.LIKE_POST,
                    postId,
                    null
            );

            return likeMapper.toResponseDTO(saved);
        }
    }

    @Override
    @Transactional
    public LikeResponseDTO toggleCommentLike(Long studentId, Long commentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum bulunamadı!"));

        Optional<Like> existing = likeRepository.findByStudentIdAndCommentId(studentId, commentId);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentRepository.save(comment);
            return new LikeResponseDTO(null, null, commentId, studentId, false);
        } else {
            Like like = Like.builder()
                    .student(student)
                    .comment(comment)
                    .build();
            Like saved = likeRepository.save(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentRepository.save(comment);

            notificationService.createNotification(
                    comment.getStudent().getId(),
                    studentId,
                    NotificationType.LIKE_COMMENT,
                    null,
                    commentId
            );

            return likeMapper.toResponseDTO(saved);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPostLiked(Long studentId, Long postId) {
        return likeRepository.existsByStudentIdAndPostId(studentId, postId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCommentLiked(Long studentId, Long commentId) {
        return likeRepository.existsByStudentIdAndCommentId(studentId, commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getLikedPostsByStudentId(Long studentId, int page, int size) {
        return likeRepository.findByStudentIdAndPostIdIsNotNullOrderByCreatedAtDesc(studentId, PageRequest.of(page, size))
                .map(like -> postMapper.toResponseDTO(like.getPost(),
                        likeRepository.existsByStudentIdAndPostId(studentId, like.getPost().getId())));
    }
}
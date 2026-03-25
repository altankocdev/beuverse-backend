package com.altankoc.beuverse_backend.like.service;

import com.altankoc.beuverse_backend.comment.entity.Comment;
import com.altankoc.beuverse_backend.comment.repository.CommentRepository;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.enums.Department;
import com.altankoc.beuverse_backend.enums.PostTag;
import com.altankoc.beuverse_backend.enums.Role;
import com.altankoc.beuverse_backend.like.dto.LikeResponseDTO;
import com.altankoc.beuverse_backend.like.entity.Like;
import com.altankoc.beuverse_backend.like.mapper.LikeMapper;
import com.altankoc.beuverse_backend.like.repository.LikeRepository;
import com.altankoc.beuverse_backend.notification.service.NotificationService;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.post.mapper.PostMapper;
import com.altankoc.beuverse_backend.post.repository.PostRepository;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock private LikeRepository likeRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private LikeMapper likeMapper;
    @Mock private PostMapper postMapper;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private LikeServiceImpl likeService;

    private Student student;
    private Student postOwner;
    private Post post;
    private Comment comment;
    private Like like;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L).firstName("Altan").lastName("Koç")
                .username("altankoc").email("altan@karaelmas.edu.tr")
                .department(Department.MUHENDISLIK_FAKULTESI)
                .role(Role.STUDENT).emailVerified(true).deleted(false)
                .build();

        postOwner = Student.builder()
                .id(2L).firstName("Fatih").lastName("Aktaş")
                .username("fatihakts").email("fatih@karaelmas.edu.tr")
                .department(Department.MUHENDISLIK_FAKULTESI)
                .role(Role.STUDENT).emailVerified(true).deleted(false)
                .build();

        post = Post.builder()
                .id(1L).content("Test post").tag(PostTag.SOSYAL)
                .likeCount(0).commentCount(0).student(postOwner)
                .images(new ArrayList<>()).comments(new ArrayList<>()).likes(new ArrayList<>())
                .build();

        comment = Comment.builder()
                .id(1L).content("Test yorum").likeCount(0).replyCount(0)
                .student(postOwner).post(post).likes(new ArrayList<>())
                .build();

        like = Like.builder()
                .id(1L).student(student).post(post)
                .build();
    }

    // ==================== TOGGLE POST LIKE ====================

    @Test
    @DisplayName("Post beğenme - yeni like eklenmeli")
    void togglePostLike_ShouldAddLike_WhenNotLiked() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByStudentIdAndPostId(1L, 1L)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(likeMapper.toResponseDTO(like)).thenReturn(
                new LikeResponseDTO(1L, 1L, null, 1L, true)
        );

        LikeResponseDTO result = likeService.togglePostLike(1L, 1L);

        assertThat(result.liked()).isTrue();
        assertThat(result.postId()).isEqualTo(1L);
        verify(likeRepository).save(any(Like.class));
        verify(notificationService).createNotification(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Post beğeniyi geri alma - like silinmeli")
    void togglePostLike_ShouldRemoveLike_WhenAlreadyLiked() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByStudentIdAndPostId(1L, 1L)).thenReturn(Optional.of(like));

        LikeResponseDTO result = likeService.togglePostLike(1L, 1L);

        assertThat(result.liked()).isFalse();
        verify(likeRepository).delete(like);
        verify(notificationService, never()).createNotification(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Post bulunamazsa hata fırlatmalı")
    void togglePostLike_ShouldThrowResourceNotFoundException_WhenPostNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.togglePostLike(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post bulunamadı!");
    }

    @Test
    @DisplayName("Öğrenci bulunamazsa hata fırlatmalı")
    void togglePostLike_ShouldThrowResourceNotFoundException_WhenStudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.togglePostLike(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Öğrenci bulunamadı!");
    }

    // ==================== TOGGLE COMMENT LIKE ====================

    @Test
    @DisplayName("Yorum beğenme - yeni like eklenmeli")
    void toggleCommentLike_ShouldAddLike_WhenNotLiked() {
        Like commentLike = Like.builder().id(1L).student(student).comment(comment).build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeRepository.findByStudentIdAndCommentId(1L, 1L)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(commentLike);
        when(likeMapper.toResponseDTO(commentLike)).thenReturn(
                new LikeResponseDTO(1L, null, 1L, 1L, true)
        );

        LikeResponseDTO result = likeService.toggleCommentLike(1L, 1L);

        assertThat(result.liked()).isTrue();
        assertThat(result.commentId()).isEqualTo(1L);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    @DisplayName("Yorum beğeniyi geri alma - like silinmeli")
    void toggleCommentLike_ShouldRemoveLike_WhenAlreadyLiked() {
        Like commentLike = Like.builder().id(1L).student(student).comment(comment).build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeRepository.findByStudentIdAndCommentId(1L, 1L)).thenReturn(Optional.of(commentLike));

        LikeResponseDTO result = likeService.toggleCommentLike(1L, 1L);

        assertThat(result.liked()).isFalse();
        verify(likeRepository).delete(commentLike);
    }

    // ==================== IS LIKED ====================

    @Test
    @DisplayName("Post beğenildi mi kontrolü")
    void isPostLiked_ShouldReturnTrue_WhenLiked() {
        when(likeRepository.existsByStudentIdAndPostId(1L, 1L)).thenReturn(true);

        boolean result = likeService.isPostLiked(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Yorum beğenildi mi kontrolü")
    void isCommentLiked_ShouldReturnTrue_WhenLiked() {
        when(likeRepository.existsByStudentIdAndCommentId(1L, 1L)).thenReturn(true);

        boolean result = likeService.isCommentLiked(1L, 1L);

        assertThat(result).isTrue();
    }
}
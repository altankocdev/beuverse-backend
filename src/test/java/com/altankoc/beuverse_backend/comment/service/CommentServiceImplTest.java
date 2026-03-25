package com.altankoc.beuverse_backend.comment.service;

import com.altankoc.beuverse_backend.comment.dto.CommentRequestDTO;
import com.altankoc.beuverse_backend.comment.dto.CommentResponseDTO;
import com.altankoc.beuverse_backend.comment.entity.Comment;
import com.altankoc.beuverse_backend.comment.mapper.CommentMapper;
import com.altankoc.beuverse_backend.comment.repository.CommentRepository;
import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.enums.Department;
import com.altankoc.beuverse_backend.enums.PostTag;
import com.altankoc.beuverse_backend.enums.Role;
import com.altankoc.beuverse_backend.like.repository.LikeRepository;
import com.altankoc.beuverse_backend.notification.service.NotificationService;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.post.repository.PostRepository;
import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CommentMapper commentMapper;
    @Mock private NotificationService notificationService;
    @Mock private LikeRepository likeRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Student student;
    private Student postOwner;
    private Post post;
    private Comment comment;
    private CommentRequestDTO commentRequestDTO;
    private CommentResponseDTO commentResponseDTO;

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
                .student(student).post(post).likes(new ArrayList<>())
                .build();

        commentRequestDTO = new CommentRequestDTO(1L, "Test yorum", null);

        commentResponseDTO = new CommentResponseDTO(
                1L, "Test yorum", 0, 0,
                new StudentSummaryDTO(1L, "Altan", "Koç", "altankoc", null),
                null, 1L, "fatihakts", false, LocalDateTime.now()
        );
    }

    // ==================== CREATE COMMENT ====================

    @Test
    @DisplayName("Başarılı yorum oluşturma")
    void createComment_ShouldReturnCommentResponseDTO_WhenValidInput() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(commentRequestDTO)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDTO(comment)).thenReturn(commentResponseDTO);

        CommentResponseDTO result = commentService.createComment(1L, commentRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Test yorum");
        verify(commentRepository).save(any(Comment.class));
        verify(notificationService).createNotification(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Öğrenci bulunamazsa hata fırlatmalı")
    void createComment_ShouldThrowResourceNotFoundException_WhenStudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(1L, commentRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Öğrenci bulunamadı!");

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Post bulunamazsa hata fırlatmalı")
    void createComment_ShouldThrowResourceNotFoundException_WhenPostNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(1L, commentRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post bulunamadı!");

        verify(commentRepository, never()).save(any());
    }

    // ==================== GET COMMENT ====================

    @Test
    @DisplayName("ID ile yorum getirme")
    void getCommentById_ShouldReturnCommentResponseDTO_WhenCommentExists() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toResponseDTO(comment)).thenReturn(commentResponseDTO);

        CommentResponseDTO result = commentService.getCommentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Olmayan yorum getirilince hata fırlatmalı")
    void getCommentById_ShouldThrowResourceNotFoundException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getCommentById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Yorum bulunamadı!");
    }

    // ==================== UPDATE COMMENT ====================

    @Test
    @DisplayName("Başka kullanıcının yorumunu güncelleyince hata fırlatmalı")
    void updateComment_ShouldThrowBusinessException_WhenStudentIsNotOwner() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.updateComment(1L, 99L, commentRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Bu yorumu düzenleme yetkiniz yok!");

        verify(commentRepository, never()).save(any());
    }

    // ==================== DELETE COMMENT ====================

    @Test
    @DisplayName("Başarılı yorum silme")
    void deleteComment_ShouldDeleteComment_WhenStudentIsOwner() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 1L);

        verify(commentRepository).delete(comment);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Başka kullanıcının yorumunu silince hata fırlatmalı")
    void deleteComment_ShouldThrowBusinessException_WhenStudentIsNotOwner() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(commentRepository, never()).delete(any());
    }
}
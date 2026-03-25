package com.altankoc.beuverse_backend.post.service;

import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.enums.Department;
import com.altankoc.beuverse_backend.enums.PostTag;
import com.altankoc.beuverse_backend.enums.Role;
import com.altankoc.beuverse_backend.like.repository.LikeRepository;
import com.altankoc.beuverse_backend.post.dto.PostRequestDTO;
import com.altankoc.beuverse_backend.post.dto.PostResponseDTO;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.post.mapper.PostMapper;
import com.altankoc.beuverse_backend.post.repository.PostImageRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Student student;
    private Post post;
    private PostRequestDTO postRequestDTO;
    private PostResponseDTO postResponseDTO;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .firstName("Altan")
                .lastName("Koç")
                .username("altankoc")
                .email("altan@beun.edu.tr")
                .password("encodedPassword")
                .department(Department.MUHENDISLIK_FAKULTESI)
                .role(Role.STUDENT)
                .emailVerified(true)
                .deleted(false)
                .build();

        post = Post.builder()
                .id(1L)
                .content("Test içeriği")
                .tag(PostTag.SOSYAL)
                .likeCount(0)
                .commentCount(0)
                .student(student)
                .images(new ArrayList<>())
                .comments(new ArrayList<>())
                .likes(new ArrayList<>())
                .build();

        postRequestDTO = new PostRequestDTO("Test içeriği", PostTag.SOSYAL, List.of());

        postResponseDTO = new PostResponseDTO(
                1L, "Test içeriği", "SOSYAL", 0, 0,
                List.of(), null, false, LocalDateTime.now()
        );
    }

    // ==================== CREATE POST ====================

    @Test
    @DisplayName("Başarılı post oluşturma")
    void createPost_ShouldReturnPostResponseDTO_WhenValidInput() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(postMapper.toEntity(postRequestDTO)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toResponseDTO(any(Post.class), anyBoolean())).thenReturn(postResponseDTO);

        PostResponseDTO result = postService.createPost(1L, postRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Test içeriği");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Öğrenci bulunamazsa hata fırlatmalı")
    void createPost_ShouldThrowResourceNotFoundException_WhenStudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost(1L, postRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Öğrenci bulunamadı!");

        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("4'ten fazla görsel eklenirse hata fırlatmalı")
    void createPost_ShouldThrowBusinessException_WhenMoreThan4Images() {
        PostRequestDTO dtoWith5Images = new PostRequestDTO(
                "Test", PostTag.SOSYAL,
                List.of("url1", "url2", "url3", "url4", "url5")
        );
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> postService.createPost(1L, dtoWith5Images))
                .isInstanceOf(BusinessException.class)
                .hasMessage("En fazla 4 görsel eklenebilir!");

        verify(postRepository, never()).save(any());
    }

    // ==================== GET POST ====================

    @Test
    @DisplayName("ID ile post getirme")
    void getPostById_ShouldReturnPostResponseDTO_WhenPostExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toResponseDTO(any(Post.class), anyBoolean())).thenReturn(postResponseDTO);

        PostResponseDTO result = postService.getPostById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Olmayan post getirilince hata fırlatmalı")
    void getPostById_ShouldThrowResourceNotFoundException_WhenPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post bulunamadı!");
    }

    // ==================== DELETE POST ====================

    @Test
    @DisplayName("Başarılı post silme")
    void deletePost_ShouldDeletePost_WhenStudentIsOwner() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, 1L);

        verify(postImageRepository).deleteByPostId(1L);
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("Başka kullanıcının postunu silince hata fırlatmalı")
    void deletePost_ShouldThrowBusinessException_WhenStudentIsNotOwner() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(1L, 99L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Bu postu silme yetkiniz yok!");

        verify(postRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Olmayan post silinince hata fırlatmalı")
    void deletePost_ShouldThrowResourceNotFoundException_WhenPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePost(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post bulunamadı!");
    }

    // ==================== UPDATE POST ====================

    @Test
    @DisplayName("Başka kullanıcının postunu güncelleyince hata fırlatmalı")
    void updatePost_ShouldThrowBusinessException_WhenStudentIsNotOwner() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.updatePost(1L, 99L, postRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Bu postu düzenleme yetkiniz yok!");

        verify(postRepository, never()).save(any());
    }
}
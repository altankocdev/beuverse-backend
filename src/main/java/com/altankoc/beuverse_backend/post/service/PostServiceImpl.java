package com.altankoc.beuverse_backend.post.service;

import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.core.security.SecurityUtils;
import com.altankoc.beuverse_backend.enums.PostTag;
import com.altankoc.beuverse_backend.like.repository.LikeRepository;
import com.altankoc.beuverse_backend.post.dto.PostRequestDTO;
import com.altankoc.beuverse_backend.post.dto.PostResponseDTO;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.post.entity.PostImage;
import com.altankoc.beuverse_backend.post.mapper.PostMapper;
import com.altankoc.beuverse_backend.post.repository.PostImageRepository;
import com.altankoc.beuverse_backend.post.repository.PostRepository;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final StudentRepository studentRepository;
    private final PostMapper postMapper;
    private final LikeRepository likeRepository;

    private boolean isLikedByCurrentStudent(Long postId) {
        try {
            Long currentStudentId = SecurityUtils.getCurrentStudentId();
            return likeRepository.existsByStudentIdAndPostId(currentStudentId, postId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public PostResponseDTO createPost(Long studentId, PostRequestDTO dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));

        if (dto.imageUrls() != null && dto.imageUrls().size() > 4) {
            throw new BusinessException("En fazla 4 görsel eklenebilir!");
        }

        Post post = postMapper.toEntity(dto);
        post.setStudent(student);

        if (dto.imageUrls() != null && !dto.imageUrls().isEmpty()) {
            AtomicInteger order = new AtomicInteger(1);
            List<PostImage> images = dto.imageUrls().stream()
                    .map(url -> PostImage.builder()
                            .imageUrl(url)
                            .imageOrder(order.getAndIncrement())
                            .post(post)
                            .build())
                    .collect(Collectors.toList());
            post.getImages().addAll(images);
        }

        Post saved = postRepository.save(post);
        return postMapper.toResponseDTO(saved, isLikedByCurrentStudent(saved.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post bulunamadı!"));
        return postMapper.toResponseDTO(post, isLikedByCurrentStudent(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(post -> postMapper.toResponseDTO(post, isLikedByCurrentStudent(post.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getPostsByStudentId(Long studentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findByStudentId(studentId, pageable)
                .map(post -> postMapper.toResponseDTO(post, isLikedByCurrentStudent(post.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDTO> getPostsByTag(PostTag tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByTag(tag, pageable)
                .map(post -> postMapper.toResponseDTO(post, isLikedByCurrentStudent(post.getId())));
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long id, Long studentId, PostRequestDTO dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post bulunamadı!"));

        if (!post.getStudent().getId().equals(studentId)) {
            throw new BusinessException("Bu postu düzenleme yetkiniz yok!");
        }

        if (dto.imageUrls() != null && dto.imageUrls().size() > 4) {
            throw new BusinessException("En fazla 4 görsel eklenebilir!");
        }

        post.setContent(dto.content());
        post.setTag(dto.tag());

        postImageRepository.deleteByPostId(id);
        if (dto.imageUrls() != null && !dto.imageUrls().isEmpty()) {
            AtomicInteger order = new AtomicInteger(1);
            List<PostImage> images = dto.imageUrls().stream()
                    .map(url -> PostImage.builder()
                            .imageUrl(url)
                            .imageOrder(order.getAndIncrement())
                            .post(post)
                            .build())
                    .collect(Collectors.toList());
            post.getImages().addAll(images);
        }

        Post saved = postRepository.save(post);
        return postMapper.toResponseDTO(saved, isLikedByCurrentStudent(saved.getId()));
    }

    @Override
    @Transactional
    public void deletePost(Long id, Long studentId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post bulunamadı!"));

        if (!post.getStudent().getId().equals(studentId)) {
            throw new BusinessException("Bu postu silme yetkiniz yok!");
        }

        postImageRepository.deleteByPostId(id);
        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDTO> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findByContentContainingIgnoreCase(keyword, pageable)
                .map(post -> postMapper.toResponseDTO(post, isLikedByCurrentStudent(post.getId())));
    }
}
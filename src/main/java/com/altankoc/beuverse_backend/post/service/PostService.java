package com.altankoc.beuverse_backend.post.service;

import com.altankoc.beuverse_backend.enums.PostTag;
import com.altankoc.beuverse_backend.post.dto.PostRequestDTO;
import com.altankoc.beuverse_backend.post.dto.PostResponseDTO;
import org.springframework.data.domain.Page;

public interface PostService {

    PostResponseDTO createPost(Long studentId, PostRequestDTO dto);
    PostResponseDTO getPostById(Long id);
    Page<PostResponseDTO> getAllPosts(int page, int size);
    Page<PostResponseDTO> getPostsByStudentId(Long studentId, int page, int size);
    Page<PostResponseDTO> getPostsByTag(PostTag tag, int page, int size);
    PostResponseDTO updatePost(Long id, Long studentId, PostRequestDTO dto);
    void deletePost(Long id, Long studentId);
    Page<PostResponseDTO> searchPosts(String keyword, int page, int size);
}
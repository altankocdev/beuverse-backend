package com.altankoc.beuverse_backend.post.controller;

import com.altankoc.beuverse_backend.core.security.SecurityUtils;
import com.altankoc.beuverse_backend.enums.PostTag;
import com.altankoc.beuverse_backend.post.dto.PostRequestDTO;
import com.altankoc.beuverse_backend.post.dto.PostResponseDTO;
import com.altankoc.beuverse_backend.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(SecurityUtils.getCurrentStudentId(), dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByStudentId(studentId, page, size));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByTag(
            @PathVariable PostTag tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByTag(tag, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestDTO dto) {
        return ResponseEntity.ok(postService.updatePost(id, SecurityUtils.getCurrentStudentId(), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id, SecurityUtils.getCurrentStudentId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDTO>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPosts(keyword, page, size));
    }
}
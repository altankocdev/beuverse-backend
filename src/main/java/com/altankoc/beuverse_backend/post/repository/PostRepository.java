package com.altankoc.beuverse_backend.post.repository;

import com.altankoc.beuverse_backend.enums.PostTag;
import com.altankoc.beuverse_backend.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"student", "images"})
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"student", "images"})
    Page<Post> findByStudentId(Long studentId, Pageable pageable);

    @EntityGraph(attributePaths = {"student", "images"})
    Page<Post> findByTag(PostTag tag, Pageable pageable);

    @EntityGraph(attributePaths = {"student", "images"})
    Page<Post> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
}
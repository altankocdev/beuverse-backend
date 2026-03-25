package com.altankoc.beuverse_backend.like.repository;

import com.altankoc.beuverse_backend.like.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByStudentIdAndPostId(Long studentId, Long postId);
    Optional<Like> findByStudentIdAndCommentId(Long studentId, Long commentId);
    boolean existsByStudentIdAndPostId(Long studentId, Long postId);
    boolean existsByStudentIdAndCommentId(Long studentId, Long commentId);
    
    @EntityGraph(attributePaths = {"post", "post.student", "post.images"})
    Page<Like> findByStudentIdAndPostIdIsNotNullOrderByCreatedAtDesc(Long studentId, Pageable pageable);
}
package com.altankoc.beuverse_backend.comment.repository;

import com.altankoc.beuverse_backend.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"student"})
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    @EntityGraph(attributePaths = {"student"})
    List<Comment> findByParentCommentId(Long parentCommentId);

    @EntityGraph(attributePaths = {"student", "post", "post.student"})
    Page<Comment> findByStudentIdOrderByCreatedAtDesc(Long studentId, Pageable pageable);
}

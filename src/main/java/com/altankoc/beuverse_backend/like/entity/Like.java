package com.altankoc.beuverse_backend.like.entity;

import com.altankoc.beuverse_backend.comment.entity.Comment;
import com.altankoc.beuverse_backend.core.base.BaseEntity;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "post_id"}, name = "uk_like_student_post"),
                @UniqueConstraint(columnNames = {"student_id", "comment_id"}, name = "uk_like_student_comment")
        },
        indexes = {
                @Index(columnList = "student_id", name = "idx_like_student_id"),
                @Index(columnList = "post_id", name = "idx_like_post_id"),
                @Index(columnList = "comment_id", name = "idx_like_comment_id")
        }
)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Like extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
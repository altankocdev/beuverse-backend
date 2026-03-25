package com.altankoc.beuverse_backend.comment.entity;

import com.altankoc.beuverse_backend.core.base.BaseEntity;
import com.altankoc.beuverse_backend.like.entity.Like;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.student.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(columnList = "post_id", name = "idx_comment_post_id"),
                @Index(columnList = "student_id", name = "idx_comment_student_id")
        }
)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @NotBlank(message = "Yorum boş bırakılamaz!")
    @Size(min = 1, max = 300, message = "Yorum en fazla 300 karakter olabilir!")
    @Column(name = "content", nullable = false, length = 300)
    private String content;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    @Column(name = "reply_count", nullable = false)
    @Builder.Default
    private int replyCount = 0;

}
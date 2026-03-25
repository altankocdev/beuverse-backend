package com.altankoc.beuverse_backend.post.entity;

import com.altankoc.beuverse_backend.comment.entity.Comment;
import com.altankoc.beuverse_backend.core.base.BaseEntity;
import com.altankoc.beuverse_backend.enums.PostTag;
import com.altankoc.beuverse_backend.like.entity.Like;
import com.altankoc.beuverse_backend.student.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "posts",
        indexes = {
                @Index(columnList = "student_id", name = "idx_post_student_id"),
                @Index(columnList = "tag", name = "idx_post_tag")
        }
)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    @NotBlank(message = "İçerik boş bırakılamaz!")
    @Size(min = 1, max = 500, message = "İçerik en fazla 500 karakter olabilir!")
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @NotNull(message = "Etiket boş bırakılamaz!")
    @Enumerated(EnumType.STRING)
    @Column(name = "tag", nullable = false)
    @Builder.Default
    private PostTag tag = PostTag.SOSYAL;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private int commentCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();
}
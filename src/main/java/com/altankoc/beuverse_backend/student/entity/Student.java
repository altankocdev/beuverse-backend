package com.altankoc.beuverse_backend.student.entity;

import com.altankoc.beuverse_backend.comment.entity.Comment;
import com.altankoc.beuverse_backend.core.base.BaseEntity;
import com.altankoc.beuverse_backend.enums.Department;
import com.altankoc.beuverse_backend.enums.Role;
import com.altankoc.beuverse_backend.like.entity.Like;
import com.altankoc.beuverse_backend.post.entity.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "student_email", name = "uk_student_email"),
                @UniqueConstraint(columnNames = "username", name = "uk_student_username")
        },
        indexes = {
                @Index(columnList = "student_email", name = "idx_student_email"),
                @Index(columnList = "username", name = "idx_student_username")
        }
)
@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {

    @NotBlank(message = "Ad alanı boş bırakılamaz!")
    @Size(min = 2, max = 30, message = "Ad 2 ile 30 karakter arasında olmalıdır!")
    @Column(name = "student_firstname", nullable = false, length = 30)
    private String firstName;

    @NotBlank(message = "Soyad alanı boş bırakılamaz!")
    @Size(min = 2, max = 30, message = "Soyad 2 ile 30 karakter arasında olmalıdır!")
    @Column(name = "student_lastname", nullable = false, length = 30)
    private String lastName;

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz!")
    @Size(min = 3, max = 20, message = "Kullanıcı adı 3 ile 20 karakter arasında olmalıdır!")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Kullanıcı adı yalnızca harf, rakam ve alt çizgi içerebilir!")
    @Column(name = "username", nullable = false, length = 20, unique = true)
    private String username;

    @NotBlank(message = "E-posta alanı boş bırakılamaz!")
    @Email(message = "Geçerli bir e-posta adresi giriniz!")
    @Column(name = "student_email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Şifre alanı boş bırakılamaz!")
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 160, message = "Biyografi en fazla 160 karakter olabilir!")
    @Column(name = "bio", length = 160)
    private String bio;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @NotNull(message = "Bölüm alanı boş bırakılamaz!")
    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private Role role = Role.STUDENT;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();
}
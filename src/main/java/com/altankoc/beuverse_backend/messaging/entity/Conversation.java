package com.altankoc.beuverse_backend.messaging.entity;

import com.altankoc.beuverse_backend.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student1_id", nullable = false)
    private Student student1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student2_id", nullable = false)
    private Student student2;

    @Column(name = "is_accepted", nullable = false)
    private boolean accepted = false;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}
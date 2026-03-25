package com.altankoc.beuverse_backend.messaging.repository;

import com.altankoc.beuverse_backend.messaging.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.student1.id = :s1 AND c.student2.id = :s2) OR (c.student1.id = :s2 AND c.student2.id = :s1)")
    Optional<Conversation> findByStudents(Long s1, Long s2);

    @Query("SELECT c FROM Conversation c WHERE c.student1.id = :studentId OR c.student2.id = :studentId ORDER BY c.createdAt DESC")
    List<Conversation> findAllByStudentId(Long studentId);

    List<Conversation> findByExpiresAtBeforeAndAcceptedTrue(LocalDateTime now);
}
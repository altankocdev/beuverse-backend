package com.altankoc.beuverse_backend.notification.repository;

import com.altankoc.beuverse_backend.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @EntityGraph(attributePaths = {"senderStudent"})
    Page<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId, Pageable pageable);

    long countByStudentIdAndReadFalse(Long studentId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.student.id = :studentId AND n.read = false")
    void markAllAsReadByStudentId(Long studentId);
}
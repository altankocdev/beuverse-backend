package com.altankoc.beuverse_backend.messaging.repository;

import com.altankoc.beuverse_backend.messaging.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
    long countByConversationIdAndReadFalseAndSenderIdNot(Long conversationId, Long senderId);

    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.conversation.id = :conversationId AND m.sender.id <> :senderId AND m.read = false")
    void markAllAsReadByConversationIdAndSenderIdNot(@Param("conversationId") Long conversationId, @Param("senderId") Long senderId);
}
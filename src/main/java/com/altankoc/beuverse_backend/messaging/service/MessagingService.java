package com.altankoc.beuverse_backend.messaging.service;

import com.altankoc.beuverse_backend.messaging.dto.ConversationResponseDTO;
import com.altankoc.beuverse_backend.messaging.dto.MessageRequestDTO;
import com.altankoc.beuverse_backend.messaging.dto.MessageResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MessagingService {

    ConversationResponseDTO startConversation(Long currentStudentId, Long otherStudentId);
    ConversationResponseDTO acceptConversation(Long conversationId, Long currentStudentId);
    List<ConversationResponseDTO> getConversations(Long currentStudentId);
    Page<MessageResponseDTO> getMessages(Long conversationId, Long currentStudentId, int page, int size);
    MessageResponseDTO sendMessage(Long conversationId, Long currentStudentId, MessageRequestDTO dto);
    void deleteConversation(Long conversationId, Long currentStudentId);
    void markMessagesAsRead(Long conversationId, Long currentStudentId);
}
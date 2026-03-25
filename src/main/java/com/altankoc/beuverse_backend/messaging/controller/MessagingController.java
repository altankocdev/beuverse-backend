package com.altankoc.beuverse_backend.messaging.controller;

import com.altankoc.beuverse_backend.core.security.SecurityUtils;
import com.altankoc.beuverse_backend.messaging.dto.ConversationResponseDTO;
import com.altankoc.beuverse_backend.messaging.dto.MessageRequestDTO;
import com.altankoc.beuverse_backend.messaging.dto.MessageResponseDTO;
import com.altankoc.beuverse_backend.messaging.service.MessagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class MessagingController {

    private final MessagingService messagingService;

    @PostMapping("/conversations/{otherStudentId}")
    public ResponseEntity<ConversationResponseDTO> startConversation(@PathVariable Long otherStudentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messagingService.startConversation(SecurityUtils.getCurrentStudentId(), otherStudentId));
    }

    @PutMapping("/conversations/{conversationId}/accept")
    public ResponseEntity<ConversationResponseDTO> acceptConversation(@PathVariable Long conversationId) {
        return ResponseEntity.ok(messagingService.acceptConversation(conversationId, SecurityUtils.getCurrentStudentId()));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponseDTO>> getConversations() {
        return ResponseEntity.ok(messagingService.getConversations(SecurityUtils.getCurrentStudentId()));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<MessageResponseDTO>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(messagingService.getMessages(conversationId, SecurityUtils.getCurrentStudentId(), page, size));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody MessageRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messagingService.sendMessage(conversationId, SecurityUtils.getCurrentStudentId(), dto));
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversationId) {
        messagingService.deleteConversation(conversationId, SecurityUtils.getCurrentStudentId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable Long conversationId) {
        messagingService.markMessagesAsRead(conversationId, SecurityUtils.getCurrentStudentId());
        return ResponseEntity.noContent().build();
    }
}
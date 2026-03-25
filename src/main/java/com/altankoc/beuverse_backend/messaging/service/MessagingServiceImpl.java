package com.altankoc.beuverse_backend.messaging.service;

import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.core.exception.UnauthorizedException;
import com.altankoc.beuverse_backend.core.websocket.WebSocketNotificationService;
import com.altankoc.beuverse_backend.messaging.dto.ConversationResponseDTO;
import com.altankoc.beuverse_backend.messaging.dto.MessageRequestDTO;
import com.altankoc.beuverse_backend.messaging.dto.MessageResponseDTO;
import com.altankoc.beuverse_backend.messaging.entity.Conversation;
import com.altankoc.beuverse_backend.messaging.entity.Message;
import com.altankoc.beuverse_backend.messaging.mapper.MessagingMapper;
import com.altankoc.beuverse_backend.messaging.repository.ConversationRepository;
import com.altankoc.beuverse_backend.messaging.repository.MessageRepository;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final StudentRepository studentRepository;
    private final MessagingMapper messagingMapper;
    private final StudentMapper studentMapper;
    private final WebSocketNotificationService webSocketNotificationService;

    @Override
    @Transactional
    public ConversationResponseDTO startConversation(Long currentStudentId, Long otherStudentId) {
        if (currentStudentId.equals(otherStudentId)) {
            throw new BusinessException("Kendinizle konuşma başlatamazsınız!");
        }

        Student currentStudent = studentRepository.findById(currentStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));
        Student otherStudent = studentRepository.findById(otherStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));

        conversationRepository.findByStudents(currentStudentId, otherStudentId)
                .ifPresent(c -> { throw new BusinessException("Bu öğrenciyle zaten aktif bir konuşmanız var!"); });

        Conversation conversation = Conversation.builder()
                .student1(currentStudent)
                .student2(otherStudent)
                .accepted(false)
                .build();

        Conversation saved = conversationRepository.save(conversation);

        ConversationResponseDTO dtoForReceiver = toConversationResponseDTO(saved, otherStudentId);
        webSocketNotificationService.sendConversationUpdate(otherStudentId, dtoForReceiver);

        return toConversationResponseDTO(saved, currentStudentId);
    }

    @Override
    @Transactional
    public ConversationResponseDTO acceptConversation(Long conversationId, Long currentStudentId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Konuşma bulunamadı!"));

        if (!conversation.getStudent2().getId().equals(currentStudentId)) {
            throw new UnauthorizedException("Bu konuşmayı onaylama yetkiniz yok!");
        }

        if (conversation.isAccepted()) {
            throw new BusinessException("Bu konuşma zaten onaylanmış!");
        }

        conversation.setAccepted(true);
        conversation.setAcceptedAt(LocalDateTime.now());
        conversation.setExpiresAt(LocalDateTime.now().plusHours(24));

        Conversation saved = conversationRepository.save(conversation);

        Long requesterId = saved.getStudent1().getId();
        ConversationResponseDTO dtoForRequester = toConversationResponseDTO(saved, requesterId);
        webSocketNotificationService.sendConversationUpdate(requesterId, dtoForRequester);

        return toConversationResponseDTO(saved, currentStudentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponseDTO> getConversations(Long currentStudentId) {
        return conversationRepository.findAllByStudentId(currentStudentId)
                .stream()
                .map(c -> toConversationResponseDTO(c, currentStudentId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponseDTO> getMessages(Long conversationId, Long currentStudentId, int page, int size) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Konuşma bulunamadı!"));

        if (!isParticipant(conversation, currentStudentId)) {
            throw new UnauthorizedException("Bu konuşmaya erişim yetkiniz yok!");
        }

        if (!conversation.isAccepted()) {
            throw new BusinessException("Konuşma henüz onaylanmadı!");
        }

        if (conversation.getExpiresAt() != null && conversation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Bu konuşmanın süresi dolmuştur!");
        }

        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, PageRequest.of(page, size))
                .map(messagingMapper::toMessageResponseDTO);
    }

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(Long conversationId, Long currentStudentId, MessageRequestDTO dto) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Konuşma bulunamadı!"));

        if (!isParticipant(conversation, currentStudentId)) {
            throw new UnauthorizedException("Bu konuşmaya erişim yetkiniz yok!");
        }

        if (!conversation.isAccepted()) {
            throw new BusinessException("Konuşma henüz onaylanmadı!");
        }

        if (conversation.getExpiresAt() != null && conversation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Bu konuşmanın süresi dolmuştur!");
        }

        Student sender = studentRepository.findById(currentStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(dto.content())
                .build();

        MessageResponseDTO saved = messagingMapper.toMessageResponseDTO(messageRepository.save(message));

        Long receiverId = conversation.getStudent1().getId().equals(currentStudentId)
                ? conversation.getStudent2().getId()
                : conversation.getStudent1().getId();

        webSocketNotificationService.sendMessage(receiverId, saved);

        ConversationResponseDTO dtoForReceiver = toConversationResponseDTO(conversation, receiverId);
        webSocketNotificationService.sendConversationUpdate(receiverId, dtoForReceiver);

        return saved;
    }

    @Override
    @Transactional
    public void deleteConversation(Long conversationId, Long currentStudentId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Konuşma bulunamadı!"));

        if (!isParticipant(conversation, currentStudentId)) {
            throw new UnauthorizedException("Bu konuşmayı silme yetkiniz yok!");
        }

        conversationRepository.delete(conversation);
    }

    private boolean isParticipant(Conversation conversation, Long studentId) {
        return conversation.getStudent1().getId().equals(studentId) ||
                conversation.getStudent2().getId().equals(studentId);
    }

    private ConversationResponseDTO toConversationResponseDTO(Conversation conversation, Long currentStudentId) {
        Student otherStudent = conversation.getStudent1().getId().equals(currentStudentId)
                ? conversation.getStudent2()
                : conversation.getStudent1();

        long unreadCount = messageRepository.countByConversationIdAndReadFalseAndSenderIdNot(
                conversation.getId(), currentStudentId);

        return new ConversationResponseDTO(
                conversation.getId(),
                studentMapper.toSummaryDTO(otherStudent),
                conversation.isAccepted(),
                conversation.getExpiresAt(),
                conversation.getCreatedAt(),
                unreadCount,
                conversation.getStudent1().getId().equals(currentStudentId)
        );
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long conversationId, Long currentStudentId) {
        messageRepository.markAllAsReadByConversationIdAndSenderIdNot(conversationId, currentStudentId);
    }
}
package com.altankoc.beuverse_backend.messaging.service;

import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.core.exception.UnauthorizedException;
import com.altankoc.beuverse_backend.core.websocket.WebSocketNotificationService;
import com.altankoc.beuverse_backend.enums.Department;
import com.altankoc.beuverse_backend.enums.Role;
import com.altankoc.beuverse_backend.messaging.dto.ConversationResponseDTO;
import com.altankoc.beuverse_backend.messaging.dto.MessageRequestDTO;
import com.altankoc.beuverse_backend.messaging.dto.MessageResponseDTO;
import com.altankoc.beuverse_backend.messaging.entity.Conversation;
import com.altankoc.beuverse_backend.messaging.entity.Message;
import com.altankoc.beuverse_backend.messaging.mapper.MessagingMapper;
import com.altankoc.beuverse_backend.messaging.repository.ConversationRepository;
import com.altankoc.beuverse_backend.messaging.repository.MessageRepository;
import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingServiceImplTest {

    @Mock private ConversationRepository conversationRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private MessagingMapper messagingMapper;
    @Mock private StudentMapper studentMapper;
    @Mock private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private MessagingServiceImpl messagingService;

    private Student student1;
    private Student student2;
    private Conversation conversation;
    private Conversation acceptedConversation;

    @BeforeEach
    void setUp() {
        student1 = Student.builder()
                .id(1L).firstName("Altan").lastName("Koç")
                .username("altankoc").email("altan@karaelmas.edu.tr")
                .department(Department.MUHENDISLIK_FAKULTESI)
                .role(Role.STUDENT).emailVerified(true).deleted(false)
                .build();

        student2 = Student.builder()
                .id(2L).firstName("Fatih").lastName("Aktaş")
                .username("fatihakts").email("fatih@karaelmas.edu.tr")
                .department(Department.MUHENDISLIK_FAKULTESI)
                .role(Role.STUDENT).emailVerified(true).deleted(false)
                .build();

        conversation = Conversation.builder()
                .id(1L).student1(student1).student2(student2)
                .accepted(false).build();

        acceptedConversation = Conversation.builder()
                .id(1L).student1(student1).student2(student2)
                .accepted(true).expiresAt(LocalDateTime.now().plusHours(24))
                .acceptedAt(LocalDateTime.now()).build();
    }

    // ==================== START CONVERSATION ====================

    @Test
    @DisplayName("Başarılı konuşma başlatma")
    void startConversation_ShouldReturnConversationResponseDTO_WhenValidInput() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student2));
        when(conversationRepository.findByStudents(1L, 2L)).thenReturn(Optional.empty());
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(messageRepository.countByConversationIdAndReadFalseAndSenderIdNot(anyLong(), anyLong())).thenReturn(0L);
        when(studentMapper.toSummaryDTO(any(Student.class))).thenReturn(
                new StudentSummaryDTO(2L, "Fatih", "Aktaş", "fatihakts", null)
        );

        ConversationResponseDTO result = messagingService.startConversation(1L, 2L);

        assertThat(result).isNotNull();
        assertThat(result.accepted()).isFalse();
        verify(conversationRepository).save(any(Conversation.class));
        verify(webSocketNotificationService).sendConversationUpdate(eq(2L), any());
    }

    @Test
    @DisplayName("Kendisiyle konuşma başlatınca hata fırlatmalı")
    void startConversation_ShouldThrowBusinessException_WhenSameStudent() {
        assertThatThrownBy(() -> messagingService.startConversation(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Kendinizle konuşma başlatamazsınız!");

        verify(conversationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Zaten aktif konuşma varsa hata fırlatmalı")
    void startConversation_ShouldThrowBusinessException_WhenConversationAlreadyExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student2));
        when(conversationRepository.findByStudents(1L, 2L)).thenReturn(Optional.of(conversation));

        assertThatThrownBy(() -> messagingService.startConversation(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Bu öğrenciyle zaten aktif bir konuşmanız var!");
    }

    // ==================== ACCEPT CONVERSATION ====================

    @Test
    @DisplayName("Başarılı konuşma onaylama")
    void acceptConversation_ShouldAcceptConversation_WhenStudent2Accepts() {
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(acceptedConversation);
        when(messageRepository.countByConversationIdAndReadFalseAndSenderIdNot(anyLong(), anyLong())).thenReturn(0L);
        when(studentMapper.toSummaryDTO(any(Student.class))).thenReturn(
                new StudentSummaryDTO(1L, "Altan", "Koç", "altankoc", null)
        );

        ConversationResponseDTO result = messagingService.acceptConversation(1L, 2L);

        assertThat(result).isNotNull();
        verify(conversationRepository).save(any(Conversation.class));
        verify(webSocketNotificationService).sendConversationUpdate(eq(1L), any());
    }

    @Test
    @DisplayName("Student1 onaylayınca hata fırlatmalı")
    void acceptConversation_ShouldThrowUnauthorizedException_WhenStudent1Accepts() {
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        assertThatThrownBy(() -> messagingService.acceptConversation(1L, 1L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Bu konuşmayı onaylama yetkiniz yok!");
    }

    @Test
    @DisplayName("Zaten onaylanmış konuşmayı tekrar onaylayınca hata fırlatmalı")
    void acceptConversation_ShouldThrowBusinessException_WhenAlreadyAccepted() {
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(acceptedConversation));

        assertThatThrownBy(() -> messagingService.acceptConversation(1L, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Bu konuşma zaten onaylanmış!");
    }

    // ==================== SEND MESSAGE ====================

    @Test
    @DisplayName("Başarılı mesaj gönderme")
    void sendMessage_ShouldSendMessage_WhenConversationAccepted() {
        Message message = Message.builder()
                .id(1L).conversation(acceptedConversation)
                .sender(student1).content("Merhaba").build();

        MessageResponseDTO messageResponseDTO = new MessageResponseDTO(
                1L, "Merhaba",
                new StudentSummaryDTO(1L, "Altan", "Koç", "altankoc", null),
                false, LocalDateTime.now()
        );

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(acceptedConversation));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messagingMapper.toMessageResponseDTO(message)).thenReturn(messageResponseDTO);
        when(messageRepository.countByConversationIdAndReadFalseAndSenderIdNot(anyLong(), anyLong())).thenReturn(0L);
        when(studentMapper.toSummaryDTO(any(Student.class))).thenReturn(
                new StudentSummaryDTO(2L, "Fatih", "Aktaş", "fatihakts", null)
        );

        MessageResponseDTO result = messagingService.sendMessage(1L, 1L, new MessageRequestDTO("Merhaba"));

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("Merhaba");
        verify(messageRepository).save(any(Message.class));
        verify(webSocketNotificationService).sendMessage(eq(2L), any());
    }

    @Test
    @DisplayName("Onaylanmamış konuşmaya mesaj gönderince hata fırlatmalı")
    void sendMessage_ShouldThrowBusinessException_WhenConversationNotAccepted() {
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        assertThatThrownBy(() -> messagingService.sendMessage(1L, 1L, new MessageRequestDTO("Merhaba")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Konuşma henüz onaylanmadı!");
    }

    @Test
    @DisplayName("Süresi dolmuş konuşmaya mesaj gönderince hata fırlatmalı")
    void sendMessage_ShouldThrowBusinessException_WhenConversationExpired() {
        Conversation expiredConversation = Conversation.builder()
                .id(1L).student1(student1).student2(student2)
                .accepted(true).expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(expiredConversation));

        assertThatThrownBy(() -> messagingService.sendMessage(1L, 1L, new MessageRequestDTO("Merhaba")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Bu konuşmanın süresi dolmuştur!");
    }

    // ==================== DELETE CONVERSATION ====================

    @Test
    @DisplayName("Başarılı konuşma silme")
    void deleteConversation_ShouldDeleteConversation_WhenParticipant() {
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        messagingService.deleteConversation(1L, 1L);

        verify(conversationRepository).delete(conversation);
    }

    @Test
    @DisplayName("Konuşmaya dahil olmayan kullanıcı silince hata fırlatmalı")
    void deleteConversation_ShouldThrowUnauthorizedException_WhenNotParticipant() {
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        assertThatThrownBy(() -> messagingService.deleteConversation(1L, 99L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Bu konuşmayı silme yetkiniz yok!");
    }
}
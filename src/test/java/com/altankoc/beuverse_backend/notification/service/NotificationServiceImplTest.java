package com.altankoc.beuverse_backend.notification.service;

import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.core.websocket.WebSocketNotificationService;
import com.altankoc.beuverse_backend.enums.Department;
import com.altankoc.beuverse_backend.enums.NotificationType;
import com.altankoc.beuverse_backend.enums.Role;
import com.altankoc.beuverse_backend.notification.dto.NotificationResponseDTO;
import com.altankoc.beuverse_backend.notification.entity.Notification;
import com.altankoc.beuverse_backend.notification.mapper.NotificationMapper;
import com.altankoc.beuverse_backend.notification.repository.NotificationRepository;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private NotificationMapper notificationMapper;
    @Mock private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Student student;
    private Student sender;
    private Notification notification;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L).firstName("Altan").lastName("Koç")
                .username("altankoc").email("altan@karaelmas.edu.tr")
                .department(Department.MUHENDISLIK_FAKULTESI)
                .role(Role.STUDENT).emailVerified(true).deleted(false)
                .build();

        sender = Student.builder()
                .id(2L).firstName("Fatih").lastName("Aktaş")
                .username("fatihakts").email("fatih@karaelmas.edu.tr")
                .department(Department.MUHENDISLIK_FAKULTESI)
                .role(Role.STUDENT).emailVerified(true).deleted(false)
                .build();

        notification = Notification.builder()
                .id(1L).student(student).senderStudent(sender)
                .type(NotificationType.LIKE_POST).postId(1L).commentId(null)
                .read(false)
                .build();
    }

    // ==================== CREATE NOTIFICATION ====================

    @Test
    @DisplayName("Başarılı bildirim oluşturma")
    void createNotification_ShouldSaveNotification_WhenDifferentStudents() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(sender));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.toResponseDTO(notification)).thenReturn(mock(NotificationResponseDTO.class));

        notificationService.createNotification(1L, 2L, NotificationType.LIKE_POST, 1L, null);

        verify(notificationRepository).save(any(Notification.class));
        verify(webSocketNotificationService).sendNotification(eq(1L), any());
    }

    @Test
    @DisplayName("Aynı öğrenciye bildirim gönderilmemeli")
    void createNotification_ShouldNotSave_WhenSameStudent() {
        notificationService.createNotification(1L, 1L, NotificationType.LIKE_POST, 1L, null);

        verify(notificationRepository, never()).save(any());
        verify(webSocketNotificationService, never()).sendNotification(any(), any());
    }

    @Test
    @DisplayName("Öğrenci bulunamazsa hata fırlatmalı")
    void createNotification_ShouldThrowResourceNotFoundException_WhenStudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                notificationService.createNotification(1L, 2L, NotificationType.LIKE_POST, 1L, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Öğrenci bulunamadı!");
    }

    // ==================== MARK AS READ ====================

    @Test
    @DisplayName("Bildirimi okundu işaretleme")
    void markAsRead_ShouldMarkNotificationAsRead_WhenOwner() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L, 1L);

        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Başka kullanıcının bildirimini okundu işaretleyince hata fırlatmalı")
    void markAsRead_ShouldThrowResourceNotFoundException_WhenNotOwner() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Bildirim bulunamadı!");
    }

    @Test
    @DisplayName("Olmayan bildirim okundu işaretlenince hata fırlatmalı")
    void markAsRead_ShouldThrowResourceNotFoundException_WhenNotificationNotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Bildirim bulunamadı!");
    }

    // ==================== UNREAD COUNT ====================

    @Test
    @DisplayName("Okunmamış bildirim sayısı")
    void getUnreadCount_ShouldReturnCount() {
        when(notificationRepository.countByStudentIdAndReadFalse(1L)).thenReturn(5L);

        long result = notificationService.getUnreadCount(1L);

        assertThat(result).isEqualTo(5L);
    }

    // ==================== MARK ALL AS READ ====================

    @Test
    @DisplayName("Tüm bildirimleri okundu işaretleme")
    void markAllAsRead_ShouldCallRepository() {
        notificationService.markAllAsRead(1L);

        verify(notificationRepository).markAllAsReadByStudentId(1L);
    }
}
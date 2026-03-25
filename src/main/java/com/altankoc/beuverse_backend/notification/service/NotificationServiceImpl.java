package com.altankoc.beuverse_backend.notification.service;

import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.core.websocket.WebSocketNotificationService;
import com.altankoc.beuverse_backend.enums.NotificationType;
import com.altankoc.beuverse_backend.notification.dto.NotificationResponseDTO;
import com.altankoc.beuverse_backend.notification.entity.Notification;
import com.altankoc.beuverse_backend.notification.mapper.NotificationMapper;
import com.altankoc.beuverse_backend.notification.repository.NotificationRepository;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final NotificationMapper notificationMapper;
    private final WebSocketNotificationService webSocketNotificationService;

    @Override
    @Transactional
    public void createNotification(Long studentId, Long senderStudentId, NotificationType type, Long postId, Long commentId) {
        if (studentId.equals(senderStudentId)) return;

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));
        Student sender = studentRepository.findById(senderStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("Gönderen bulunamadı!"));

        Notification notification = Notification.builder()
                .student(student)
                .senderStudent(sender)
                .type(type)
                .postId(postId)
                .commentId(commentId)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponseDTO dto = notificationMapper.toResponseDTO(saved);
        webSocketNotificationService.sendNotification(studentId, dto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getNotifications(Long studentId, int page, int size) {
        return notificationRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId, PageRequest.of(page, size))
                .map(notificationMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long studentId) {
        return notificationRepository.countByStudentIdAndReadFalse(studentId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long studentId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Bildirim bulunamadı!"));

        if (!notification.getStudent().getId().equals(studentId)) {
            throw new ResourceNotFoundException("Bildirim bulunamadı!");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long studentId) {
        notificationRepository.markAllAsReadByStudentId(studentId);
    }
}
package com.altankoc.beuverse_backend.core.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(Long studentId, Object payload) {
        log.info("Mesaj gönderiliyor → studentId: {}", studentId);
        messagingTemplate.convertAndSend(
                "/queue/messages-" + studentId,
                payload
        );
    }

    public void sendNotification(Long studentId, Object payload) {
        log.info("Bildirim gönderiliyor → studentId: {}", studentId);
        messagingTemplate.convertAndSend(
                "/queue/notifications-" + studentId,
                payload
        );
    }

    public void sendConversationUpdate(Long studentId, Object payload) {
        log.info("Konuşma güncellemesi gönderiliyor → studentId: {}", studentId);
        messagingTemplate.convertAndSend(
                "/queue/conversations-" + studentId,
                payload
        );
    }
}
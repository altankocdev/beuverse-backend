package com.altankoc.beuverse_backend.core.scheduler;

import com.altankoc.beuverse_backend.messaging.repository.ConversationRepository;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanupScheduler {

    private final StudentRepository studentRepository;
    private final ConversationRepository conversationRepository;

    @Scheduled(cron = "0 0 2 * * SUN")
    @Transactional
    public void hardDeleteOldSoftDeletedStudents() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        log.info("Hesap temizliği başladı: {}", LocalDateTime.now());
        studentRepository.deleteByDeletedTrueAndUpdatedAtBefore(threshold);
        log.info("Hesap temizliği tamamlandı: {}", LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupUnverifiedStudents() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        studentRepository.deleteByEmailVerifiedFalseAndCreatedAtBefore(threshold);
        log.info("Doğrulanmamış hesaplar temizlendi.");
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredConversations() {
        List<com.altankoc.beuverse_backend.messaging.entity.Conversation> expired =
                conversationRepository.findByExpiresAtBeforeAndAcceptedTrue(LocalDateTime.now());

        if (!expired.isEmpty()) {
            conversationRepository.deleteAll(expired);
            log.info("{} adet süresi dolmuş konuşma silindi.", expired.size());
        }
    }
}
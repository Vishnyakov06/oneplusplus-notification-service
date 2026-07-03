package com.hh.oneplusplus.service;

import com.hh.oneplusplus.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationCleanUpScheduler {
    private final NotificationRepository notificationRepository;
    private static final Logger log = LoggerFactory.getLogger(NotificationCleanUpScheduler.class);

    @Value("${app.notification.cleanup.ttl-days:7}")
    private int ttlDays;

    public NotificationCleanUpScheduler(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(cron = "0 0 */12 * * *")
    @Transactional
    public void cleanUpOldReadNotifications() {
        int deletedCount = notificationRepository.deleteReadBefore(Instant.now()
                .minus(ttlDays, ChronoUnit.DAYS));
        log.info("Cleanup finished. Successfully deleted {} read notifications.", deletedCount);
    }
}

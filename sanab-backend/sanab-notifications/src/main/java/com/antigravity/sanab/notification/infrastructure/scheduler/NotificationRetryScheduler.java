package com.antigravity.sanab.notification.infrastructure.scheduler;

import com.antigravity.sanab.notification.application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job that retries failed notifications.
 *
 * <p>Runs every 5 minutes and finds all {@link com.antigravity.sanab.notification.domain.enums.NotificationStatus#FAILED}
 * notifications whose {@code nextRetryAt} timestamp is in the past.
 *
 * <p>Retry scheduling:
 * <ul>
 *   <li>CRITICAL: immediate → 30s → 60s (max 3 retries)</li>
 *   <li>HIGH: 30s → 60s → 120s (max 3 retries)</li>
 *   <li>NORMAL: 5min → 10min → 20min (max 3 retries)</li>
 *   <li>LOW: 1hr → 2hr (max 2 retries)</li>
 * </ul>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRetryScheduler {

    private final NotificationService notificationService;

    /**
     * Retry job — runs every 5 minutes.
     *
     * <p>Fixed delay ensures previous execution completes before next starts.
     */
    @Scheduled(fixedDelay = 300_000, initialDelay = 60_000) // 5 minutes, starts after 1 minute
    public void retryFailedNotifications() {
        log.debug("NotificationRetryScheduler: checking for retryable notifications");
        try {
            notificationService.retryFailedNotifications();
        } catch (Exception ex) {
            log.error("NotificationRetryScheduler encountered an error: {}", ex.getMessage(), ex);
        }
    }
}

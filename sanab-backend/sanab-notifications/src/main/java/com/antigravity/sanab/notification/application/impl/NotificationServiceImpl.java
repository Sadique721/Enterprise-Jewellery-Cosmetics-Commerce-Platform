package com.antigravity.sanab.notification.application.impl;

import com.antigravity.sanab.notification.api.dto.response.NotificationResponse;
import com.antigravity.sanab.notification.application.port.NotificationChannelPort;
import com.antigravity.sanab.notification.application.port.NotificationDeliveryException;
import com.antigravity.sanab.notification.application.port.TemplateEnginePort;
import com.antigravity.sanab.notification.application.service.NotificationPreferenceService;
import com.antigravity.sanab.notification.application.service.NotificationService;
import com.antigravity.sanab.notification.domain.entity.Notification;
import com.antigravity.sanab.notification.domain.entity.NotificationPreference;
import com.antigravity.sanab.notification.domain.entity.NotificationTemplate;
import com.antigravity.sanab.notification.domain.enums.*;
import com.antigravity.sanab.notification.domain.repository.NotificationRepository;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * Implementation of {@link NotificationService}.
 *
 * <p>Orchestrates the complete notification pipeline:
 * <ol>
 *   <li>Receive domain event</li>
 *   <li>Resolve applicable channels (event defaults + user preferences + security override)</li>
 *   <li>For each channel: create {@link Notification} record (QUEUED)</li>
 *   <li>Render content via template engine</li>
 *   <li>Dispatch via channel adapter (Spring Retry for resilience)</li>
 *   <li>Update status (SENT / FAILED / PERMANENTLY_FAILED)</li>
 *   <li>Log outcome</li>
 * </ol>
 *
 * <p><strong>Security override rule:</strong> SECURITY category notifications
 * are ALWAYS dispatched, regardless of user preference settings.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceService preferenceService;
    private final TemplateEnginePort templateEngine;
    private final List<NotificationChannelPort> channelPorts;
    private final ObjectMapper objectMapper;

    // ─── Event Processing ─────────────────────────────────────────────────────

    @Override
    public void processEvent(SanabNotificationEvent event) {
        log.info("Processing notification event: type={}, userId={}",
                event.eventType(), event.userId());

        NotificationEventType eventType = event.eventType();
        Set<NotificationChannel> channels = resolveChannels(event);

        if (channels.isEmpty()) {
            log.debug("No channels resolved for event type={}, userId={}. Skipping.",
                    eventType, event.userId());
            return;
        }

        // Build metadata JSON for audit/debugging
        String metadata = serializeEventMetadata(event);

        for (NotificationChannel channel : channels) {
            try {
                Notification notification = createNotificationRecord(event, channel, metadata);
                dispatchWithRetry(notification);
            } catch (Exception ex) {
                // Each channel is independent: failure on one doesn't stop others
                log.error("Failed to process channel {} for event {}: {}",
                        channel, eventType, ex.getMessage());
            }
        }
    }

    // ─── Channel Resolution ───────────────────────────────────────────────────

    /**
     * Determines which channels to use for this event.
     *
     * <p>Resolution logic:
     * <ol>
     *   <li>Start with the event type's default channels</li>
     *   <li>For SECURITY category: use all default channels (preference ignored)</li>
     *   <li>For other categories: filter by user preference</li>
     * </ol>
     */
    private Set<NotificationChannel> resolveChannels(SanabNotificationEvent event) {
        NotificationEventType eventType = event.eventType();
        Set<NotificationChannel> defaultChannels = eventType.getDefaultChannels();

        // Security notifications always delivered regardless of preferences
        if (eventType.getCategory() == NotificationCategory.SECURITY) {
            log.debug("Security event — bypassing preference check for event={}", eventType);
            return defaultChannels;
        }

        // No user ID means broadcast (Flash Sale, etc.) — use defaults
        if (event.userId() == null) {
            return defaultChannels;
        }

        // Filter by user preferences
        NotificationPreference prefs = preferenceService.getOrCreatePreference(event.userId());
        Set<NotificationChannel> allowedChannels = EnumSet.noneOf(NotificationChannel.class);

        for (NotificationChannel channel : defaultChannels) {
            if (isChannelAllowedByPreference(channel, eventType.getCategory(), prefs)) {
                allowedChannels.add(channel);
            }
        }

        return allowedChannels;
    }

    private boolean isChannelAllowedByPreference(NotificationChannel channel,
                                                  NotificationCategory category,
                                                  NotificationPreference prefs) {
        // Channel-level check
        boolean channelEnabled = switch (channel) {
            case EMAIL      -> prefs.isEmailEnabled();
            case SMS        -> prefs.isSmsEnabled();
            case WHATSAPP   -> prefs.isWhatsAppEnabled();
            case IN_APP     -> prefs.isInAppEnabled();
        };

        if (!channelEnabled) return false;

        // Category-level check
        return switch (category) {
            case SECURITY       -> true; // Already handled above, but defensive
            case TRANSACTIONAL  -> prefs.isTransactionalNotificationsEnabled();
            case MARKETING      -> prefs.isMarketingEnabled();
            case PRODUCT_ALERTS -> prefs.isProductAlertsEnabled();
            case SYSTEM         -> prefs.isSystemNotificationsEnabled();
            case ADMIN          -> true; // Admin notifications always delivered to admins
        };
    }

    // ─── Notification Record Creation ─────────────────────────────────────────

    private Notification createNotificationRecord(SanabNotificationEvent event,
                                                   NotificationChannel channel,
                                                   String metadata) {
        NotificationEventType eventType = event.eventType();
        NotificationPriority priority = eventType.getDefaultPriority();

        // Determine recipient address based on channel
        String recipientAddress = switch (channel) {
            case EMAIL    -> event.email();
            case SMS      -> event.phone();
            case WHATSAPP -> event.phone();
            case IN_APP   -> event.userId() != null ? event.userId().toString() : null;
        };

        if (recipientAddress == null || recipientAddress.isBlank()) {
            log.warn("Skipping {} notification for event={}: recipient address is null",
                    channel, eventType);
            throw new IllegalStateException(
                    "Cannot create %s notification: recipient address is null for event %s"
                            .formatted(channel, eventType));
        }

        Notification notification = Notification.builder()
                .userId(event.userId())
                .recipientAddress(recipientAddress)
                .eventType(eventType)
                .channel(channel)
                .priority(priority)
                .category(eventType.getCategory())
                .locale("en") // Resolved from user profile in future iterations
                .status(NotificationStatus.QUEUED)
                .maxRetries(priority.getMaxRetries())
                .metadata(metadata)
                .build();

        return notificationRepository.save(notification);
    }

    // ─── Dispatch with Retry ──────────────────────────────────────────────────

    @Override
    public void dispatch(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalStateException(
                        "Notification not found for dispatch: " + notificationId));
        dispatchWithRetry(notification);
    }

    @Retryable(
            retryFor = NotificationDeliveryException.class,
            maxAttemptsExpression = "#{notification.maxRetries + 1}",
            backoff = @Backoff(
                    delayExpression = "#{notification.priority.initialDelaySeconds * 1000L}",
                    multiplier = 2.0,
                    maxDelay = 300000L // 5 minutes max
            )
    )
    private void dispatchWithRetry(Notification notification) {
        notification.setStatus(NotificationStatus.PROCESSING);
        notificationRepository.save(notification);

        // Render content via template engine
        try {
            NotificationTemplate template = templateEngine.resolveTemplate(
                    notification.getEventType(),
                    notification.getChannel(),
                    notification.getLocale()
            );
            notification.setTemplateId(template.getId());

            // For now, content is set by channel adapters via template rendering
        } catch (Exception e) {
            log.warn("Template resolution failed for event={} channel={}: {}",
                    notification.getEventType(), notification.getChannel(), e.getMessage());
        }

        // Find the appropriate channel adapter
        NotificationChannelPort port = channelPorts.stream()
                .filter(p -> p.supports(notification))
                .findFirst()
                .orElseThrow(() -> new NotificationDeliveryException(
                        "No channel adapter found for channel: " + notification.getChannel()));

        port.send(notification);
        notificationRepository.save(notification);

        log.info("Notification dispatched: id={}, channel={}, eventType={}, status={}",
                notification.getId(), notification.getChannel(),
                notification.getEventType(), notification.getStatus());
    }

    /**
     * Recover method called after all retry attempts are exhausted.
     * Marks notification as PERMANENTLY_FAILED.
     */
    @Recover
    private void recoverFromDeliveryFailure(NotificationDeliveryException ex,
                                            Notification notification) {
        log.error("All retry attempts exhausted for notification id={}, channel={}: {}",
                notification.getId(), notification.getChannel(), ex.getMessage());

        notification.recordFailure(ex.getMessage(), null); // null = no more retries
        notificationRepository.save(notification);
    }

    // ─── Read Management ──────────────────────────────────────────────────────

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification not found: " + notificationId));
        notification.markRead();
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllReadForUser(userId, Instant.now());
        log.debug("Marked all in-app notifications as read for userId={}", userId);
    }

    // ─── Queries ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository
                .findByUserIdAndChannelOrderByCreatedAtDesc(userId, NotificationChannel.IN_APP, pageable)
                .map(NotificationResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndChannelAndReadFalse(
                userId, NotificationChannel.IN_APP);
    }

    // ─── Retry Scheduler ──────────────────────────────────────────────────────

    @Override
    public void retryFailedNotifications() {
        Instant now = Instant.now();
        List<Notification> retryable = notificationRepository
                .findRetryableNotifications(now);

        log.info("Retry job: found {} notifications eligible for retry", retryable.size());

        for (Notification notification : retryable) {
            try {
                dispatchWithRetry(notification);
            } catch (Exception ex) {
                log.error("Retry failed for notification id={}: {}", notification.getId(), ex.getMessage());
            }
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String serializeEventMetadata(SanabNotificationEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize event metadata for type={}: {}",
                    event.eventType(), e.getMessage());
            return "{}";
        }
    }
}

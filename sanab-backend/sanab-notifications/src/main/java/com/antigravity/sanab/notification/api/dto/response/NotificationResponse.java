package com.antigravity.sanab.notification.api.dto.response;

import com.antigravity.sanab.notification.domain.entity.Notification;
import com.antigravity.sanab.notification.domain.enums.NotificationChannel;
import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.domain.enums.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * API response DTO for a single notification record.
 *
 * <p>Exposes only the fields relevant to client applications.
 * Sensitive provider internals and metadata are excluded.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public record NotificationResponse(
        UUID id,
        NotificationEventType eventType,
        String eventDisplayName,
        NotificationChannel channel,
        NotificationStatus status,
        String subject,
        String content,
        boolean read,
        Instant readAt,
        Instant sentAt,
        Instant scheduledAt,
        Instant createdAt
) {
    /**
     * Maps a {@link Notification} entity to this response DTO.
     */
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getEventType(),
                n.getEventType().getDisplayName(),
                n.getChannel(),
                n.getStatus(),
                n.getSubject(),
                n.getContent(),
                n.isRead(),
                n.getReadAt(),
                n.getSentAt(),
                n.getScheduledAt(),
                n.getCreatedAt()
        );
    }
}

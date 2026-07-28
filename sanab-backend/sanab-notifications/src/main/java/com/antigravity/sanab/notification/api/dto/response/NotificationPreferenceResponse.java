package com.antigravity.sanab.notification.api.dto.response;

import com.antigravity.sanab.notification.domain.entity.NotificationPreference;

import java.time.Instant;
import java.util.UUID;

/**
 * API response DTO for user notification preferences.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
public record NotificationPreferenceResponse(
        UUID userId,
        // Channel preferences
        boolean emailEnabled,
        boolean smsEnabled,
        boolean whatsAppEnabled,
        boolean pushEnabled,
        boolean inAppEnabled,
        // Category preferences
        boolean securityNotificationsEnabled,
        boolean transactionalNotificationsEnabled,
        boolean marketingEnabled,
        boolean productAlertsEnabled,
        boolean systemNotificationsEnabled,
        boolean newsletterEnabled,
        // Timestamps
        Instant lastUpdatedAt
) {
    public static NotificationPreferenceResponse from(NotificationPreference p) {
        return new NotificationPreferenceResponse(
                p.getUserId(),
                p.isEmailEnabled(),
                p.isSmsEnabled(),
                p.isWhatsAppEnabled(),
                p.isPushEnabled(),
                p.isInAppEnabled(),
                p.isSecurityNotificationsEnabled(),
                p.isTransactionalNotificationsEnabled(),
                p.isMarketingEnabled(),
                p.isProductAlertsEnabled(),
                p.isSystemNotificationsEnabled(),
                p.isNewsletterEnabled(),
                p.getUpdatedAt()
        );
    }
}

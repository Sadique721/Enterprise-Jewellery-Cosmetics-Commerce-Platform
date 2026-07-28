package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a user enables or disables MFA on their account.
 * Priority: HIGH — security-critical event.
 *
 * @param userId     the user modifying MFA settings
 * @param email      user's email
 * @param phone      user's phone (nullable)
 * @param firstName  user's first name
 * @param enabled    true if MFA was enabled, false if disabled
 * @param mfaMethod  the MFA method (e.g. "TOTP", "SMS")
 * @param occurredAt event creation timestamp
 */
public record MfaStatusChangedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        boolean enabled,
        String mfaMethod,
        Instant occurredAt
) implements SanabNotificationEvent {

    public MfaStatusChangedEvent(UUID userId, String email, String phone,
                                 String firstName, boolean enabled, String mfaMethod) {
        this(userId, email, phone, firstName, enabled, mfaMethod, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return enabled ? NotificationEventType.MFA_ENABLED : NotificationEventType.MFA_DISABLED;
    }
}

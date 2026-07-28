package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a user successfully changes their password.
 * Priority: CRITICAL — delivered regardless of user preferences.
 *
 * @param userId        the user who changed their password
 * @param email         user's email
 * @param phone         user's phone (nullable)
 * @param firstName     user's first name
 * @param ipAddress     IP from which the change was made
 * @param changedAt     timestamp of the password change
 * @param occurredAt    event creation timestamp
 */
public record PasswordChangedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        String ipAddress,
        Instant changedAt,
        Instant occurredAt
) implements SanabNotificationEvent {

    public PasswordChangedEvent(UUID userId, String email, String phone,
                                String firstName, String ipAddress) {
        this(userId, email, phone, firstName, ipAddress, Instant.now(), Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.PASSWORD_CHANGED;
    }
}

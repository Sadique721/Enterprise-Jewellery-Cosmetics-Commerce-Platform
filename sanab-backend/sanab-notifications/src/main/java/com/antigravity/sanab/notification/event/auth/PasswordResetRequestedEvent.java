package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a user requests a password reset link.
 *
 * <p>Triggers: Email Reset Link, SMS Alert.
 * Priority: CRITICAL — delivered immediately.
 *
 * @param userId        the user requesting password reset
 * @param email         user's email address
 * @param phone         user's phone (nullable)
 * @param firstName     user's first name
 * @param resetLink     the password reset URL (expires in 1 hour)
 * @param expiresAt     when the reset link expires
 * @param occurredAt    event creation timestamp
 */
public record PasswordResetRequestedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        String resetLink,
        Instant expiresAt,
        Instant occurredAt
) implements SanabNotificationEvent {

    public PasswordResetRequestedEvent(UUID userId, String email, String phone,
                                       String firstName, String resetLink, Instant expiresAt) {
        this(userId, email, phone, firstName, resetLink, expiresAt, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.PASSWORD_RESET_REQUESTED;
    }
}

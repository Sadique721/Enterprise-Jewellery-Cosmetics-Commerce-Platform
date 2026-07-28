package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a user account is locked due to excessive failed login attempts.
 * Priority: CRITICAL — always delivered.
 *
 * @param userId         the locked user's ID
 * @param email          user's email
 * @param phone          user's phone (nullable)
 * @param firstName      user's first name
 * @param lockedUntil    timestamp when the account lockout expires (nullable = indefinite)
 * @param supportEmail   support contact for account recovery
 * @param occurredAt     event creation timestamp
 */
public record AccountLockedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        Instant lockedUntil,
        String supportEmail,
        Instant occurredAt
) implements SanabNotificationEvent {

    public AccountLockedEvent(UUID userId, String email, String phone,
                              String firstName, Instant lockedUntil, String supportEmail) {
        this(userId, email, phone, firstName, lockedUntil, supportEmail, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.ACCOUNT_LOCKED;
    }
}

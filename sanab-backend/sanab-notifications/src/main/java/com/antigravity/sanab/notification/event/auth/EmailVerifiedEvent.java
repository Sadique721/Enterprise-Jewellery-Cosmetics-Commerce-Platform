package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a user successfully verifies their email address.
 *
 * <p>Triggers: Confirmation Email, WhatsApp Confirmation, In-App Notification.
 *
 * @param userId     the user who verified their email
 * @param email      the verified email address
 * @param firstName  user's first name for personalization
 * @param occurredAt timestamp of verification
 */
public record EmailVerifiedEvent(
        UUID userId,
        String email,
        String firstName,
        Instant occurredAt
) implements SanabNotificationEvent {

    public EmailVerifiedEvent(UUID userId, String email, String firstName) {
        this(userId, email, firstName, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.EMAIL_VERIFIED;
    }
}

package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a user successfully verifies their phone number.
 *
 * <p>Triggers: Email Confirmation, SMS Confirmation, WhatsApp Confirmation.
 *
 * @param userId     the user who verified their phone
 * @param email      user's email for cross-channel confirmation
 * @param phone      the verified phone number in E.164 format
 * @param firstName  user's first name for personalization
 * @param occurredAt timestamp of verification
 */
public record PhoneVerifiedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        Instant occurredAt
) implements SanabNotificationEvent {

    public PhoneVerifiedEvent(UUID userId, String email, String phone, String firstName) {
        this(userId, email, phone, firstName, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.PHONE_VERIFIED;
    }
}

package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a new user successfully creates an account.
 *
 * <p>Triggers:
 * <ul>
 *   <li>Welcome Email (with email verification link)</li>
 *   <li>SMS Welcome Message</li>
 *   <li>WhatsApp Welcome Message</li>
 *   <li>In-App Welcome Notification</li>
 * </ul>
 *
 * <p>Published by: {@code sanab-identity} AuthService after user persist.
 *
 * @param userId        the newly registered user's UUID
 * @param email         the user's email address
 * @param phone         the user's phone number in E.164 format (nullable)
 * @param firstName     the user's first name for personalization
 * @param verificationLink the email verification URL (expires in 24h)
 * @param occurredAt    timestamp of event creation
 */
public record UserRegisteredEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        String verificationLink,
        Instant occurredAt
) implements SanabNotificationEvent {

    public UserRegisteredEvent(UUID userId, String email, String phone,
                               String firstName, String verificationLink) {
        this(userId, email, phone, firstName, verificationLink, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.USER_REGISTERED;
    }
}

package com.antigravity.sanab.notification.event.auth;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a login is detected from an unrecognized device.
 *
 * <p>Triggers: Security Email, SMS Alert, WhatsApp Alert, In-App Security Alert.
 * Priority: CRITICAL — always delivered regardless of user preferences.
 *
 * @param userId         the user who logged in
 * @param email          user's email
 * @param phone          user's phone (nullable)
 * @param firstName      user's first name
 * @param deviceType     detected device type (e.g. "Chrome on Windows")
 * @param ipAddress      the login IP address (sanitized for display)
 * @param location       approximate location derived from IP (e.g. "Mumbai, India")
 * @param loginAt        timestamp of the login
 * @param secureAccountLink URL for the user to secure their account
 * @param occurredAt     event creation timestamp
 */
public record NewDeviceLoginEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        String deviceType,
        String ipAddress,
        String location,
        Instant loginAt,
        String secureAccountLink,
        Instant occurredAt
) implements SanabNotificationEvent {

    public NewDeviceLoginEvent(UUID userId, String email, String phone, String firstName,
                               String deviceType, String ipAddress, String location,
                               String secureAccountLink) {
        this(userId, email, phone, firstName, deviceType, ipAddress, location,
                Instant.now(), secureAccountLink, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.NEW_DEVICE_LOGIN;
    }
}

package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Suspicious login pattern detected. Priority: CRITICAL. */
public record SuspiciousLoginEvent(
        UUID userId, String email, String phone, String firstName,
        UUID suspiciousUserId, String suspiciousUserEmail,
        String ipAddress, String location, String reason, Instant occurredAt
) implements SanabNotificationEvent {
    public SuspiciousLoginEvent(UUID suspiciousUserId, String suspiciousUserEmail,
                                String ipAddress, String location, String reason) {
        this(null, null, null, "Admin", suspiciousUserId, suspiciousUserEmail,
                ipAddress, location, reason, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.SUSPICIOUS_LOGIN; }
}

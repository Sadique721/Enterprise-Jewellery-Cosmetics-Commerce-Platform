package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Scheduled system maintenance notice. Priority: NORMAL. */
public record SystemMaintenanceEvent(
        UUID userId, String email, String phone, String firstName,
        Instant maintenanceStart, Instant maintenanceEnd,
        String affectedServices, String message, Instant occurredAt
) implements SanabNotificationEvent {
    public SystemMaintenanceEvent(Instant maintenanceStart, Instant maintenanceEnd,
                                  String affectedServices, String message) {
        this(null, null, null, "System", maintenanceStart, maintenanceEnd,
                affectedServices, message, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.SYSTEM_MAINTENANCE; }
}

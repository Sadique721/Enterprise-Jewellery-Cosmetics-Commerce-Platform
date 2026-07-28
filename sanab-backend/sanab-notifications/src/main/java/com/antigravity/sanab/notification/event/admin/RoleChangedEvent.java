package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** User role changed by admin. Priority: HIGH. */
public record RoleChangedEvent(
        UUID userId, String email, String phone, String firstName,
        UUID targetUserId, String targetUserEmail,
        String oldRole, String newRole, String changedByAdmin, Instant occurredAt
) implements SanabNotificationEvent {
    public RoleChangedEvent(UUID targetUserId, String targetUserEmail,
                            String oldRole, String newRole, String changedByAdmin) {
        this(null, null, null, "Admin", targetUserId, targetUserEmail,
                oldRole, newRole, changedByAdmin, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.ROLE_CHANGED; }
}

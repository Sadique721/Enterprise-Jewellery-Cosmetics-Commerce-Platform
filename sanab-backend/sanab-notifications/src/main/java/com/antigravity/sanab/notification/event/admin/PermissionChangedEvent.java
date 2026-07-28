package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Permission changed for user. Priority: HIGH. */
public record PermissionChangedEvent(
        UUID userId, String email, String phone, String firstName,
        UUID targetUserId, String targetUserEmail, String permissionAction,
        String permissionName, String changedByAdmin, Instant occurredAt
) implements SanabNotificationEvent {
    public PermissionChangedEvent(UUID targetUserId, String targetUserEmail,
                                  String permissionAction, String permissionName, String changedByAdmin) {
        this(null, null, null, "Admin", targetUserId, targetUserEmail,
                permissionAction, permissionName, changedByAdmin, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.PERMISSION_CHANGED; }
}

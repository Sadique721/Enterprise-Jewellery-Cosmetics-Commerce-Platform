package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Published when admin creates a product. Triggers: Email, In-App to admin team. */
public record ProductCreatedAdminEvent(
        UUID userId, String email, String phone, String firstName,
        UUID productId, String productName, String adminName, Instant occurredAt
) implements SanabNotificationEvent {
    public ProductCreatedAdminEvent(UUID adminUserId, String adminEmail, String adminName,
                                    UUID productId, String productName) {
        this(adminUserId, adminEmail, null, adminName, productId, productName, adminName, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.ADMIN_PRODUCT_CREATED; }
}

package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Inventory falling below reorder point. Priority: HIGH. */
public record InventoryLowEvent(
        UUID userId, String email, String phone, String firstName,
        UUID productId, String productName, String sku,
        int currentQuantity, int reorderPoint, Instant occurredAt
) implements SanabNotificationEvent {
    public InventoryLowEvent(UUID productId, String productName, String sku,
                             int currentQuantity, int reorderPoint) {
        this(null, null, null, "Admin", productId, productName, sku,
                currentQuantity, reorderPoint, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.INVENTORY_LOW; }
}

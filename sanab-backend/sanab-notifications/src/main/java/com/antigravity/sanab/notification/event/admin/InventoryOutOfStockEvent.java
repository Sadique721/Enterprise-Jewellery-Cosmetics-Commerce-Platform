package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Product completely out of stock. Priority: CRITICAL. */
public record InventoryOutOfStockEvent(
        UUID userId, String email, String phone, String firstName,
        UUID productId, String productName, String sku, Instant occurredAt
) implements SanabNotificationEvent {
    public InventoryOutOfStockEvent(UUID productId, String productName, String sku) {
        this(null, null, null, "Admin", productId, productName, sku, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.INVENTORY_OUT_OF_STOCK; }
}

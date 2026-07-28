package com.antigravity.sanab.notification.event.shopping;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a product is added to the user's cart.
 * Triggers: Optional In-App notification only.
 *
 * @param userId      the user who added the item
 * @param productId   the product added
 * @param productName product display name
 * @param quantity    quantity added
 * @param occurredAt  event creation timestamp
 */
public record CartItemAddedEvent(
        UUID userId,
        UUID productId,
        String productName,
        int quantity,
        Instant occurredAt
) implements SanabNotificationEvent {

    public CartItemAddedEvent(UUID userId, UUID productId, String productName, int quantity) {
        this(userId, productId, productName, quantity, Instant.now());
    }

    @Override
    public String email() { return null; }

    @Override
    public String phone() { return null; }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.CART_ITEM_ADDED;
    }
}

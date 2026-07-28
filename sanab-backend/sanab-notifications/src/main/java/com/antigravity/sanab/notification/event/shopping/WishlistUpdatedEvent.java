package com.antigravity.sanab.notification.event.shopping;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a user's wishlist is updated.
 * Triggers: In-App notification only.
 *
 * @param userId      the user who updated their wishlist
 * @param productId   the product added/removed
 * @param productName product display name
 * @param action      "ADDED" or "REMOVED"
 * @param occurredAt  event creation timestamp
 */
public record WishlistUpdatedEvent(
        UUID userId,
        UUID productId,
        String productName,
        String action,
        Instant occurredAt
) implements SanabNotificationEvent {

    public WishlistUpdatedEvent(UUID userId, UUID productId, String productName, String action) {
        this(userId, productId, productName, action, Instant.now());
    }

    @Override
    public String email() { return null; }

    @Override
    public String phone() { return null; }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.WISHLIST_UPDATED;
    }
}

package com.antigravity.sanab.notification.event.promotional;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Wishlist reminder to prompt purchase of saved items. Triggers: Email, WhatsApp, In-App. */
public record WishlistReminderEvent(
        UUID userId, String email, String phone, String firstName,
        List<String> productNames, String wishlistUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public WishlistReminderEvent(UUID userId, String email, String phone, String firstName,
                                 List<String> productNames, String wishlistUrl) {
        this(userId, email, phone, firstName, productNames, wishlistUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.WISHLIST_REMINDER; }
}

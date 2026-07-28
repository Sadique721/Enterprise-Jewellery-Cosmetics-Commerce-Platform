package com.antigravity.sanab.notification.event.promotional;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Flash sale broadcast event. userId=null means broadcast to all opted-in users. */
public record FlashSaleEvent(
        UUID userId, String email, String phone, String firstName,
        String saleTitle, String saleDescription, int discountPercent,
        Instant saleStartsAt, Instant saleEndsAt, String shopUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public FlashSaleEvent(String saleTitle, String saleDescription, int discountPercent,
                          Instant saleStartsAt, Instant saleEndsAt, String shopUrl) {
        this(null, null, null, null, saleTitle, saleDescription,
                discountPercent, saleStartsAt, saleEndsAt, shopUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.FLASH_SALE; }
}

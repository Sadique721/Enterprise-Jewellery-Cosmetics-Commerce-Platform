package com.antigravity.sanab.notification.event.promotional;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Gift card issued. Triggers: Email, SMS, WhatsApp, In-App. */
public record GiftCardIssuedEvent(
        UUID userId, String email, String phone, String firstName,
        String giftCardCode, BigDecimal balance, String currency,
        Instant validUntil, String redeemUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public GiftCardIssuedEvent(UUID userId, String email, String phone, String firstName,
                               String giftCardCode, BigDecimal balance, String currency,
                               Instant validUntil, String redeemUrl) {
        this(userId, email, phone, firstName, giftCardCode, balance,
                currency, validUntil, redeemUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.GIFT_CARD_ISSUED; }
}

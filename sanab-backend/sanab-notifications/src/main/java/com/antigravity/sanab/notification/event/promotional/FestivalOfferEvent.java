package com.antigravity.sanab.notification.event.promotional;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Festival/seasonal offer. Triggers: Email, SMS, WhatsApp, In-App. */
public record FestivalOfferEvent(
        UUID userId, String email, String phone, String firstName,
        String festivalName, String offerTitle, String offerDescription,
        String couponCode, Instant validUntil, String shopUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public FestivalOfferEvent(UUID userId, String email, String phone, String firstName,
                              String festivalName, String offerTitle, String offerDescription,
                              String couponCode, Instant validUntil, String shopUrl) {
        this(userId, email, phone, firstName, festivalName, offerTitle,
                offerDescription, couponCode, validUntil, shopUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.FESTIVAL_OFFER; }
}

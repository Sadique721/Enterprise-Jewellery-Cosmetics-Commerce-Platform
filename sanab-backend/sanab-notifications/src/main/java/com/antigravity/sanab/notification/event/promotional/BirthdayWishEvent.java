package com.antigravity.sanab.notification.event.promotional;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Birthday greeting. Triggers: Email, WhatsApp, In-App. */
public record BirthdayWishEvent(
        UUID userId, String email, String phone, String firstName,
        String specialOfferCode, String offerUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public BirthdayWishEvent(UUID userId, String email, String phone, String firstName,
                             String specialOfferCode, String offerUrl) {
        this(userId, email, phone, firstName, specialOfferCode, offerUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.BIRTHDAY_WISH; }
}

package com.antigravity.sanab.notification.event.promotional;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/** Loyalty points awarded. Triggers: Email, WhatsApp, In-App. */
public record LoyaltyRewardEvent(
        UUID userId, String email, String phone, String firstName,
        int pointsEarned, int totalPoints, String rewardDescription,
        String redeemUrl, Instant occurredAt
) implements SanabNotificationEvent {
    public LoyaltyRewardEvent(UUID userId, String email, String phone, String firstName,
                              int pointsEarned, int totalPoints,
                              String rewardDescription, String redeemUrl) {
        this(userId, email, phone, firstName, pointsEarned, totalPoints,
                rewardDescription, redeemUrl, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.LOYALTY_REWARD; }
}

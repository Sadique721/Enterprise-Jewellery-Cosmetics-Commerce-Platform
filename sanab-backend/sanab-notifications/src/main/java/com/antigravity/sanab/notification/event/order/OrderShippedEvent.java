package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when an order is shipped and a tracking number is assigned.
 *
 * <p>Triggers: Email, SMS, WhatsApp (all with tracking details), In-App.
 *
 * @param userId            customer's user ID
 * @param email             customer's email
 * @param phone             customer's phone
 * @param firstName         customer's first name
 * @param orderId           the order UUID
 * @param orderNumber       human-readable order number
 * @param courierName       name of the shipping carrier
 * @param trackingNumber    carrier-issued tracking number
 * @param trackingUrl       live tracking URL
 * @param estimatedDelivery human-readable estimated delivery date
 * @param occurredAt        event creation timestamp
 */
public record OrderShippedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        UUID orderId,
        String orderNumber,
        String courierName,
        String trackingNumber,
        String trackingUrl,
        String estimatedDelivery,
        Instant occurredAt
) implements SanabNotificationEvent {

    public OrderShippedEvent(UUID userId, String email, String phone, String firstName,
                             UUID orderId, String orderNumber, String courierName,
                             String trackingNumber, String trackingUrl, String estimatedDelivery) {
        this(userId, email, phone, firstName, orderId, orderNumber, courierName,
                trackingNumber, trackingUrl, estimatedDelivery, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.ORDER_SHIPPED;
    }
}

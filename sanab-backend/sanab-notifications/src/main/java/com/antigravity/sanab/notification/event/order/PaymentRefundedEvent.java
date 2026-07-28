package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Published when a refund has been processed for an order.
 *
 * @param userId           customer's user ID
 * @param email            customer's email
 * @param phone            customer's phone (nullable)
 * @param firstName        customer's first name
 * @param orderId          the associated order UUID
 * @param orderNumber      human-readable order number
 * @param refundId         the refund transaction ID
 * @param refundAmount     the amount refunded
 * @param currency         ISO 4217 currency code
 * @param refundMethod     where the refund is going (e.g. "Original Payment Method")
 * @param estimatedArrival days until refund reaches the customer
 * @param occurredAt       event creation timestamp
 */
public record PaymentRefundedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        UUID orderId,
        String orderNumber,
        String refundId,
        BigDecimal refundAmount,
        String currency,
        String refundMethod,
        String estimatedArrival,
        Instant occurredAt
) implements SanabNotificationEvent {

    public PaymentRefundedEvent(UUID userId, String email, String phone, String firstName,
                                UUID orderId, String orderNumber, String refundId,
                                BigDecimal refundAmount, String currency,
                                String refundMethod, String estimatedArrival) {
        this(userId, email, phone, firstName, orderId, orderNumber, refundId,
                refundAmount, currency, refundMethod, estimatedArrival, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.PAYMENT_REFUNDED;
    }
}

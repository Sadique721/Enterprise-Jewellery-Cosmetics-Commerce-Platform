package com.antigravity.sanab.notification.event.order;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Published when a customer successfully places an order.
 *
 * <p>Triggers: Order Confirmation Email (with GST Invoice PDF), SMS, WhatsApp, In-App.
 * Priority: HIGH
 *
 * @param userId            customer's user ID
 * @param email             customer's email
 * @param phone             customer's phone (nullable)
 * @param firstName         customer's first name
 * @param orderId           the new order's UUID
 * @param orderNumber       human-readable order number (e.g. "ORD-2026-78452")
 * @param items             list of ordered items for display
 * @param subtotal          order subtotal before taxes/discounts
 * @param taxAmount         total tax applied
 * @param discountAmount    total discount applied
 * @param totalAmount       final charged amount
 * @param currency          ISO 4217 currency code
 * @param paymentStatus     e.g. "PAID", "PENDING"
 * @param paymentMethod     e.g. "Credit Card", "UPI", "COD"
 * @param deliveryAddress   formatted delivery address string
 * @param estimatedDelivery human-readable estimated delivery window
 * @param trackingUrl       URL for order tracking (may be null until shipped)
 * @param supportEmail      support contact email
 * @param orderDetailUrl    direct link to the order details page
 * @param occurredAt        event creation timestamp
 */
public record OrderPlacedEvent(
        UUID userId,
        String email,
        String phone,
        String firstName,
        UUID orderId,
        String orderNumber,
        List<OrderItem> items,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String currency,
        String paymentStatus,
        String paymentMethod,
        String deliveryAddress,
        String estimatedDelivery,
        String trackingUrl,
        String supportEmail,
        String orderDetailUrl,
        Instant occurredAt
) implements SanabNotificationEvent {

    public OrderPlacedEvent(UUID userId, String email, String phone, String firstName,
                            UUID orderId, String orderNumber, List<OrderItem> items,
                            BigDecimal subtotal, BigDecimal taxAmount, BigDecimal discountAmount,
                            BigDecimal totalAmount, String currency, String paymentStatus,
                            String paymentMethod, String deliveryAddress,
                            String estimatedDelivery, String supportEmail, String orderDetailUrl) {
        this(userId, email, phone, firstName, orderId, orderNumber, items,
                subtotal, taxAmount, discountAmount, totalAmount, currency,
                paymentStatus, paymentMethod, deliveryAddress, estimatedDelivery,
                null, supportEmail, orderDetailUrl, Instant.now());
    }

    @Override
    public NotificationEventType eventType() {
        return NotificationEventType.ORDER_PLACED;
    }

    /**
     * Immutable representation of a single ordered item for notification rendering.
     *
     * @param productName  the product name
     * @param variantLabel the variant (e.g. "22K Gold / Size 7 / Ring")
     * @param imageUrl     thumbnail URL
     * @param quantity     quantity ordered
     * @param unitPrice    price per unit
     * @param totalPrice   quantity × unitPrice
     */
    public record OrderItem(
            String productName,
            String variantLabel,
            String imageUrl,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice,
            String currency
    ) {}
}

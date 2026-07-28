package com.antigravity.sanab.orders.api.dto.response;

import com.antigravity.sanab.orders.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String orderNumber,
        UUID userId,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal grandTotal,
        String couponCode,
        String shippingFullName,
        String shippingPhone,
        String shippingAddressLine,
        String shippingCity,
        String shippingState,
        String shippingPostalCode,
        String shippingCountry,
        String carrierName,
        String trackingNumber,
        Instant estimatedDeliveryAt,
        Instant deliveredAt,
        Instant createdAt,
        List<OrderItemResponse> items
) {
    public record OrderItemResponse(UUID id, UUID productId, UUID variantId, String productName, String sku, String imageUrl, BigDecimal unitPrice, int quantity, BigDecimal itemTotal) {}
}

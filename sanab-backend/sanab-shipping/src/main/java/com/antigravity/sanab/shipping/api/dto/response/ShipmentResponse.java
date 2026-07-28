package com.antigravity.sanab.shipping.api.dto.response;

import com.antigravity.sanab.shipping.domain.enums.ShipmentStatus;

import java.time.Instant;
import java.util.UUID;

public record ShipmentResponse(
        UUID id,
        UUID orderId,
        String carrierName,
        String trackingNumber,
        ShipmentStatus status,
        String trackingUrl,
        Instant estimatedDeliveryAt,
        Instant deliveredAt,
        Instant createdAt
) {}

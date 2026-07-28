package com.antigravity.sanab.shipping.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateShipmentRequest(
        @NotNull(message = "Order ID is required")
        UUID orderId,

        @NotBlank(message = "Carrier name is required")
        @Size(max = 100)
        String carrierName,

        @NotBlank(message = "Tracking number is required")
        @Size(max = 100)
        String trackingNumber,

        String trackingUrl
) {}

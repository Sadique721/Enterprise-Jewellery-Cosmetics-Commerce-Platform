package com.antigravity.sanab.orders.api.dto.request;

import com.antigravity.sanab.orders.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status,

        String carrierName,

        String trackingNumber
) {}

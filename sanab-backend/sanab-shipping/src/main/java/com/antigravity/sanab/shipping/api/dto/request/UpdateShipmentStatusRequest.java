package com.antigravity.sanab.shipping.api.dto.request;

import com.antigravity.sanab.shipping.domain.enums.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateShipmentStatusRequest(
        @NotNull(message = "Status is required")
        ShipmentStatus status
) {}

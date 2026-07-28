package com.antigravity.sanab.inventory.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateStockRequest(
        @NotBlank(message = "SKU is required")
        String sku,

        @Min(value = 0, message = "Quantity cannot be negative")
        int availableQuantity,

        int lowStockThreshold
) {}

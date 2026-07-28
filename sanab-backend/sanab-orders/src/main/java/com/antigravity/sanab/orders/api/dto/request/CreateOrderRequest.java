package com.antigravity.sanab.orders.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank(message = "Shipping full name is required")
        @Size(max = 100)
        String shippingFullName,

        @NotBlank(message = "Shipping phone is required")
        @Size(max = 20)
        String shippingPhone,

        @NotBlank(message = "Shipping address line is required")
        @Size(max = 500)
        String shippingAddressLine,

        @NotBlank(message = "Shipping city is required")
        @Size(max = 100)
        String shippingCity,

        @NotBlank(message = "Shipping state is required")
        @Size(max = 100)
        String shippingState,

        @NotBlank(message = "Shipping postal code is required")
        @Size(max = 20)
        String shippingPostalCode,

        @NotBlank(message = "Shipping country is required")
        @Size(max = 100)
        String shippingCountry,

        String couponCode
) {}

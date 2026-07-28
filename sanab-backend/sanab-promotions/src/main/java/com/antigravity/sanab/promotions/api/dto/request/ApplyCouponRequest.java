package com.antigravity.sanab.promotions.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ApplyCouponRequest(
        @NotBlank(message = "Coupon code is required")
        String code,

        @NotNull(message = "Subtotal is required")
        @Positive(message = "Subtotal must be positive")
        BigDecimal subtotal
) {}

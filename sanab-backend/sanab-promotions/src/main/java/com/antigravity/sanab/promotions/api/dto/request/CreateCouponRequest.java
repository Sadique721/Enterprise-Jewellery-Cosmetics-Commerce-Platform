package com.antigravity.sanab.promotions.api.dto.request;

import com.antigravity.sanab.promotions.domain.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponRequest(
        @NotBlank(message = "Coupon code is required")
        @Size(max = 50)
        String code,

        String description,

        @NotNull(message = "Discount type is required")
        DiscountType discountType,

        @NotNull(message = "Discount value is required")
        @Positive(message = "Discount value must be positive")
        BigDecimal discountValue,

        BigDecimal minimumSpend,

        BigDecimal maximumDiscountAmount,

        Integer usageLimit,

        Instant validFrom,

        Instant validUntil
) {}

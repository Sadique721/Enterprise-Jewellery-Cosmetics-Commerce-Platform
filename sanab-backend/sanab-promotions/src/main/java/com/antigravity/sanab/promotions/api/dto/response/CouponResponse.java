package com.antigravity.sanab.promotions.api.dto.response;

import com.antigravity.sanab.promotions.domain.enums.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minimumSpend,
        BigDecimal maximumDiscountAmount,
        Integer usageLimit,
        int usedCount,
        Instant validFrom,
        Instant validUntil,
        boolean active,
        boolean valid
) {}

package com.antigravity.sanab.cart.api.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID id,
        UUID userId,
        String guestSessionId,
        BigDecimal subtotal,
        BigDecimal discountTotal,
        BigDecimal grandTotal,
        String appliedCouponCode,
        int totalItemCount,
        List<CartItemResponse> items
) {}

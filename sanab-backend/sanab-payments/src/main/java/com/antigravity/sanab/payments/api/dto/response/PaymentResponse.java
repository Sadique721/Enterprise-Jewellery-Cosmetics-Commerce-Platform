package com.antigravity.sanab.payments.api.dto.response;

import com.antigravity.sanab.payments.domain.enums.PaymentMethod;
import com.antigravity.sanab.payments.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID userId,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        String gatewayTransactionId,
        String failureReason,
        Instant createdAt
) {}

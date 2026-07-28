package com.antigravity.sanab.payments.api.dto.request;

import com.antigravity.sanab.payments.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessPaymentRequest(
        @NotNull(message = "Order ID is required")
        UUID orderId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        // Optional payment token (from Authorize.Net / Stripe / Razorpay SDK)
        String paymentToken,

        String cardNumber,

        String expirationMonth,

        String expirationYear,

        String cardCode
) {}

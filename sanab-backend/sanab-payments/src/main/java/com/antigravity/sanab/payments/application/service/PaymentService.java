package com.antigravity.sanab.payments.application.service;

import com.antigravity.sanab.payments.api.dto.request.ProcessPaymentRequest;
import com.antigravity.sanab.payments.api.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse processPayment(UUID userId, ProcessPaymentRequest request);

    PaymentResponse getPaymentById(UUID userId, UUID paymentId);

    List<PaymentResponse> getPaymentsForOrder(UUID userId, UUID orderId);
}

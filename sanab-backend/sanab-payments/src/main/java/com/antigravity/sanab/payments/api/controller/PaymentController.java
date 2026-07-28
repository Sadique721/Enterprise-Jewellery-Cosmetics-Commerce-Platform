package com.antigravity.sanab.payments.api.controller;

import com.antigravity.sanab.payments.api.dto.request.ProcessPaymentRequest;
import com.antigravity.sanab.payments.api.dto.response.PaymentResponse;
import com.antigravity.sanab.payments.application.service.PaymentService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing and payment transaction tracking")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Process payment for an order")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @AuthenticationPrincipal String userIdStr,
            @Valid @RequestBody ProcessPaymentRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        PaymentResponse response = paymentService.processPayment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Payment processed successfully"));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment transaction details by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID paymentId) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentById(userId, paymentId)));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get all payment transactions for an order")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsForOrder(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID orderId) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsForOrder(userId, orderId)));
    }
}

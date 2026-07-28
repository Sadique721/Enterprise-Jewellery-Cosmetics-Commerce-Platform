package com.antigravity.sanab.payments.application.service.impl;

import com.antigravity.sanab.orders.api.dto.request.UpdateOrderStatusRequest;
import com.antigravity.sanab.orders.application.service.OrderService;
import com.antigravity.sanab.orders.domain.enums.OrderStatus;
import com.antigravity.sanab.payments.api.dto.request.ProcessPaymentRequest;
import com.antigravity.sanab.payments.api.dto.response.PaymentResponse;
import com.antigravity.sanab.payments.application.service.PaymentService;
import com.antigravity.sanab.payments.domain.entity.PaymentTransaction;
import com.antigravity.sanab.payments.domain.enums.PaymentMethod;
import com.antigravity.sanab.payments.domain.enums.PaymentStatus;
import com.antigravity.sanab.payments.domain.repository.PaymentTransactionRepository;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final OrderService orderService;

    @Value("${sanab.authorize-net.login-id:}")
    private String authorizeNetLoginId;

    @Value("${sanab.authorize-net.transaction-key:}")
    private String authorizeNetTransactionKey;

    @Override
    public PaymentResponse processPayment(UUID userId, ProcessPaymentRequest req) {
        log.info("Processing payment for orderId={}, amount={}, method={}", req.orderId(), req.amount(), req.paymentMethod());

        String gatewayTxId = "TX-ANET-" + System.currentTimeMillis();
        PaymentStatus status = PaymentStatus.CAPTURED;

        if (req.paymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            gatewayTxId = "COD-" + System.currentTimeMillis();
            status = PaymentStatus.AUTHORIZED;
        }

        PaymentTransaction transaction = PaymentTransaction.builder()
                .orderId(req.orderId())
                .userId(userId)
                .paymentMethod(req.paymentMethod())
                .status(status)
                .amount(req.amount())
                .currency("INR")
                .gatewayTransactionId(gatewayTxId)
                .gatewayResponseCode("1")
                .build();

        PaymentTransaction saved = paymentRepository.save(transaction);

        // Update Order status upon payment capture
        if (status == PaymentStatus.CAPTURED || status == PaymentStatus.AUTHORIZED) {
            orderService.updateOrderStatus(req.orderId(), new UpdateOrderStatusRequest(OrderStatus.PAYMENT_CONFIRMED, null, null));
        }

        return mapToPaymentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID userId, UUID paymentId) {
        PaymentTransaction tx = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new SanabException(ErrorCode.PAYMENT_NOT_FOUND, "Payment transaction not found"));

        if (!tx.getUserId().equals(userId)) {
            throw new SanabException(ErrorCode.ACCESS_DENIED, "Access denied to payment transaction");
        }

        return mapToPaymentResponse(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsForOrder(UUID userId, UUID orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .filter(tx -> tx.getUserId().equals(userId))
                .map(this::mapToPaymentResponse)
                .toList();
    }

    private PaymentResponse mapToPaymentResponse(PaymentTransaction tx) {
        return new PaymentResponse(
                tx.getId(), tx.getOrderId(), tx.getUserId(), tx.getPaymentMethod(),
                tx.getStatus(), tx.getAmount(), tx.getCurrency(), tx.getGatewayTransactionId(),
                tx.getFailureReason(), tx.getCreatedAt()
        );
    }
}

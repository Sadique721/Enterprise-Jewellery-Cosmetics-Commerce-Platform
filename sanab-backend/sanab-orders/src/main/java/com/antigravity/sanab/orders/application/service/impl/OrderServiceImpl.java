package com.antigravity.sanab.orders.application.service.impl;

import com.antigravity.sanab.cart.domain.entity.Cart;
import com.antigravity.sanab.cart.domain.entity.CartItem;
import com.antigravity.sanab.cart.domain.repository.CartRepository;
import com.antigravity.sanab.orders.api.dto.request.CreateOrderRequest;
import com.antigravity.sanab.orders.api.dto.request.UpdateOrderStatusRequest;
import com.antigravity.sanab.orders.api.dto.response.OrderResponse;
import com.antigravity.sanab.orders.application.service.OrderService;
import com.antigravity.sanab.orders.domain.entity.Order;
import com.antigravity.sanab.orders.domain.entity.OrderItem;
import com.antigravity.sanab.orders.domain.enums.OrderStatus;
import com.antigravity.sanab.orders.domain.repository.OrderRepository;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final Random RANDOM = new Random();

    @Override
    public OrderResponse createOrderFromCart(UUID userId, CreateOrderRequest req) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new SanabException(ErrorCode.CART_NOT_FOUND, "Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new SanabException(ErrorCode.CART_EMPTY, "Cannot place order with empty cart");
        }

        String orderNumber = generateOrderNumber();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .status(OrderStatus.PENDING_PAYMENT)
                .subtotal(cart.getSubtotal())
                .shippingFee(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .discountAmount(cart.getDiscountTotal())
                .grandTotal(cart.getGrandTotal())
                .couponCode(cart.getAppliedCouponCode())
                .shippingFullName(req.shippingFullName().strip())
                .shippingPhone(req.shippingPhone().strip())
                .shippingAddressLine(req.shippingAddressLine().strip())
                .shippingCity(req.shippingCity().strip())
                .shippingState(req.shippingState().strip())
                .shippingPostalCode(req.shippingPostalCode().strip())
                .shippingCountry(req.shippingCountry().strip())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cItem : cart.getItems()) {
            orderItems.add(OrderItem.builder()
                    .order(order)
                    .productId(cItem.getProductId())
                    .variantId(cItem.getVariantId())
                    .productName(cItem.getProductName())
                    .sku(cItem.getSku())
                    .imageUrl(cItem.getImageUrl())
                    .unitPrice(cItem.getUnitPrice())
                    .quantity(cItem.getQuantity())
                    .itemTotal(cItem.getItemTotal())
                    .build());
        }

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // Clear user cart after successful order creation
        cart.getItems().clear();
        cart.recalculateTotals();
        cartRepository.save(cart);

        log.info("Created order: number={}, userId={}, grandTotal={}", orderNumber, userId, savedOrder.getGrandTotal());

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new SanabException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new SanabException(ErrorCode.ACCESS_DENIED, "Access denied to this order");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(UUID userId, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new SanabException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new SanabException(ErrorCode.ACCESS_DENIED, "Access denied to this order");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getUserOrders(UUID userId, Pageable pageable) {
        Page<OrderResponse> page = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToOrderResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(OrderStatus statusFilter, Pageable pageable) {
        Page<Order> page = statusFilter != null ?
                orderRepository.findByStatusOrderByCreatedAtDesc(statusFilter, pageable) :
                orderRepository.findAll(pageable);
        return PagedResponse.of(page.map(this::mapToOrderResponse));
    }

    @Override
    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest req) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new SanabException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));

        order.setStatus(req.status());
        if (req.carrierName() != null) order.setCarrierName(req.carrierName().strip());
        if (req.trackingNumber() != null) order.setTrackingNumber(req.trackingNumber().strip());

        if (req.status() == OrderStatus.DELIVERED) {
            order.setDeliveredAt(Instant.now());
        }

        Order saved = orderRepository.save(order);
        log.info("Updated order status: id={}, status={}", orderId, req.status());
        return mapToOrderResponse(saved);
    }

    @Override
    public void cancelOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new SanabException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new SanabException(ErrorCode.ACCESS_DENIED, "Access denied to this order");
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAYMENT_CONFIRMED) {
            throw new SanabException(ErrorCode.ORDER_CANNOT_BE_CANCELLED, "Order cannot be cancelled in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Cancelled orderId={} for userId={}", orderId, userId);
    }

    private static String generateOrderNumber() {
        return "SANAB-" + System.currentTimeMillis() + "-" + (100 + RANDOM.nextInt(900));
    }

    private OrderResponse mapToOrderResponse(Order o) {
        List<OrderResponse.OrderItemResponse> itemResponses = o.getItems() == null ? List.of() :
                o.getItems().stream()
                        .map(i -> new OrderResponse.OrderItemResponse(
                                i.getId(), i.getProductId(), i.getVariantId(), i.getProductName(),
                                i.getSku(), i.getImageUrl(), i.getUnitPrice(), i.getQuantity(), i.getItemTotal()))
                        .toList();

        return new OrderResponse(
                o.getId(), o.getOrderNumber(), o.getUserId(), o.getStatus(),
                o.getSubtotal(), o.getShippingFee(), o.getTaxAmount(), o.getDiscountAmount(),
                o.getGrandTotal(), o.getCouponCode(), o.getShippingFullName(), o.getShippingPhone(),
                o.getShippingAddressLine(), o.getShippingCity(), o.getShippingState(),
                o.getShippingPostalCode(), o.getShippingCountry(), o.getCarrierName(),
                o.getTrackingNumber(), o.getEstimatedDeliveryAt(), o.getDeliveredAt(),
                o.getCreatedAt(), itemResponses
        );
    }
}

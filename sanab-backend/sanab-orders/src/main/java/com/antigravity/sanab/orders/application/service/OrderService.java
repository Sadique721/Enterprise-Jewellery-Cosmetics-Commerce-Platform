package com.antigravity.sanab.orders.application.service;

import com.antigravity.sanab.orders.api.dto.request.CreateOrderRequest;
import com.antigravity.sanab.orders.api.dto.request.UpdateOrderStatusRequest;
import com.antigravity.sanab.orders.api.dto.response.OrderResponse;
import com.antigravity.sanab.orders.domain.enums.OrderStatus;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrderFromCart(UUID userId, CreateOrderRequest request);

    OrderResponse getOrderById(UUID userId, UUID orderId);

    OrderResponse getOrderByNumber(UUID userId, String orderNumber);

    PagedResponse<OrderResponse> getUserOrders(UUID userId, Pageable pageable);

    PagedResponse<OrderResponse> getAllOrders(OrderStatus statusFilter, Pageable pageable);

    OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request);

    void cancelOrder(UUID userId, UUID orderId);
}

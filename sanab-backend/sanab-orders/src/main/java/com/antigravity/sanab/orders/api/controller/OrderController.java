package com.antigravity.sanab.orders.api.controller;

import com.antigravity.sanab.orders.api.dto.request.CreateOrderRequest;
import com.antigravity.sanab.orders.api.dto.request.UpdateOrderStatusRequest;
import com.antigravity.sanab.orders.api.dto.response.OrderResponse;
import com.antigravity.sanab.orders.application.service.OrderService;
import com.antigravity.sanab.orders.domain.enums.OrderStatus;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order creation, order tracking, and order management")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an order from the user's active cart")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal String userIdStr,
            @Valid @RequestBody CreateOrderRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        OrderResponse response = orderService.createOrderFromCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Order created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get current user's orders (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getUserOrders(
            @AuthenticationPrincipal String userIdStr,
            @PageableDefault(size = 10) Pageable pageable) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(userId, pageable)));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by order ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID orderId) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(userId, orderId)));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID orderId) {
        UUID userId = UUID.fromString(userIdStr);
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order cancelled successfully"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: List all orders with status filter")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getAllOrdersAdmin(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(status, pageable)));
    }

    @PatchMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: Update order status & shipping tracking")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatusAdmin(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Order status updated"));
    }
}

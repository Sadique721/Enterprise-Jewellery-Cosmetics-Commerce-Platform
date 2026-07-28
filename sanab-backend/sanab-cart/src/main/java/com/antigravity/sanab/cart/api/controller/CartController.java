package com.antigravity.sanab.cart.api.controller;

import com.antigravity.sanab.cart.api.dto.request.AddToCartRequest;
import com.antigravity.sanab.cart.api.dto.request.UpdateCartItemRequest;
import com.antigravity.sanab.cart.api.dto.response.CartResponse;
import com.antigravity.sanab.cart.application.service.CartService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "Cart item management, guest cart, and user cart operations")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal String userIdStr,
            @RequestHeader(value = "X-Guest-Session-Id", required = false) String guestSessionId) {
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userId, guestSessionId)));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal String userIdStr,
            @RequestHeader(value = "X-Guest-Session-Id", required = false) String guestSessionId,
            @Valid @RequestBody AddToCartRequest request) {
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        CartResponse response = cartService.addItem(userId, guestSessionId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Item added to cart"));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @AuthenticationPrincipal String userIdStr,
            @RequestHeader(value = "X-Guest-Session-Id", required = false) String guestSessionId,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        CartResponse response = cartService.updateItemQuantity(userId, guestSessionId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cart item updated"));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal String userIdStr,
            @RequestHeader(value = "X-Guest-Session-Id", required = false) String guestSessionId,
            @PathVariable UUID itemId) {
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        CartResponse response = cartService.removeItem(userId, guestSessionId, itemId);
        return ResponseEntity.ok(ApiResponse.success(response, "Item removed from cart"));
    }

    @DeleteMapping
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal String userIdStr,
            @RequestHeader(value = "X-Guest-Session-Id", required = false) String guestSessionId) {
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;
        cartService.clearCart(userId, guestSessionId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared"));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge guest cart into authenticated user cart")
    public ResponseEntity<ApiResponse<Void>> mergeGuestCart(
            @AuthenticationPrincipal String userIdStr,
            @RequestHeader("X-Guest-Session-Id") String guestSessionId) {
        UUID userId = UUID.fromString(userIdStr);
        cartService.mergeGuestCartToUser(userId, guestSessionId);
        return ResponseEntity.ok(ApiResponse.success(null, "Guest cart merged successfully"));
    }
}

package com.antigravity.sanab.cart.application.service;

import com.antigravity.sanab.cart.api.dto.request.AddToCartRequest;
import com.antigravity.sanab.cart.api.dto.request.UpdateCartItemRequest;
import com.antigravity.sanab.cart.api.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCart(UUID userId, String guestSessionId);

    CartResponse addItem(UUID userId, String guestSessionId, AddToCartRequest request);

    CartResponse updateItemQuantity(UUID userId, String guestSessionId, UUID itemId, UpdateCartItemRequest request);

    CartResponse removeItem(UUID userId, String guestSessionId, UUID itemId);

    void clearCart(UUID userId, String guestSessionId);

    void mergeGuestCartToUser(UUID userId, String guestSessionId);
}

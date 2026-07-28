package com.antigravity.sanab.cart.application.service.impl;

import com.antigravity.sanab.cart.api.dto.request.AddToCartRequest;
import com.antigravity.sanab.cart.api.dto.request.UpdateCartItemRequest;
import com.antigravity.sanab.cart.api.dto.response.CartItemResponse;
import com.antigravity.sanab.cart.api.dto.response.CartResponse;
import com.antigravity.sanab.cart.application.service.CartService;
import com.antigravity.sanab.cart.domain.entity.Cart;
import com.antigravity.sanab.cart.domain.entity.CartItem;
import com.antigravity.sanab.cart.domain.repository.CartItemRepository;
import com.antigravity.sanab.cart.domain.repository.CartRepository;
import com.antigravity.sanab.catalog.domain.entity.Product;
import com.antigravity.sanab.catalog.domain.repository.ProductRepository;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public CartResponse getCart(UUID userId, String guestSessionId) {
        Cart cart = getOrCreateCart(userId, guestSessionId);
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse addItem(UUID userId, String guestSessionId, AddToCartRequest req) {
        Cart cart = getOrCreateCart(userId, guestSessionId);
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new SanabException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found"));

        BigDecimal unitPrice = product.getSalePrice() != null ? product.getSalePrice() : product.getBasePrice();
        String mainImageUrl = product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0).getUrl() : null;

        Optional<CartItem> existingItemOpt = req.variantId() != null
                ? cartItemRepository.findByCartIdAndProductIdAndVariantId(cart.getId(), req.productId(), req.variantId())
                : cartItemRepository.findByCartIdAndProductId(cart.getId(), req.productId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + req.quantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .variantId(req.variantId())
                    .productName(product.getTitle())
                    .sku(product.getSku())
                    .imageUrl(mainImageUrl)
                    .unitPrice(unitPrice)
                    .quantity(req.quantity())
                    .build();

            cart.getItems().add(newItem);
        }

        cart.recalculateTotals();
        Cart saved = cartRepository.save(cart);
        log.info("Added item to cartId={}, productId={}, qty={}", cart.getId(), req.productId(), req.quantity());
        return mapToCartResponse(saved);
    }

    @Override
    public CartResponse updateItemQuantity(UUID userId, String guestSessionId, UUID itemId, UpdateCartItemRequest req) {
        Cart cart = getOrCreateCart(userId, guestSessionId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new SanabException(ErrorCode.CART_ITEM_NOT_FOUND, "Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new SanabException(ErrorCode.ACCESS_DENIED, "Item does not belong to your cart");
        }

        item.setQuantity(req.quantity());
        cartItemRepository.save(item);
        cart.recalculateTotals();
        Cart saved = cartRepository.save(cart);
        return mapToCartResponse(saved);
    }

    @Override
    public CartResponse removeItem(UUID userId, String guestSessionId, UUID itemId) {
        Cart cart = getOrCreateCart(userId, guestSessionId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new SanabException(ErrorCode.CART_ITEM_NOT_FOUND, "Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new SanabException(ErrorCode.ACCESS_DENIED, "Item does not belong to your cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cart.recalculateTotals();
        Cart saved = cartRepository.save(cart);
        log.info("Removed itemId={} from cartId={}", itemId, cart.getId());
        return mapToCartResponse(saved);
    }

    @Override
    public void clearCart(UUID userId, String guestSessionId) {
        Cart cart = getOrCreateCart(userId, guestSessionId);
        cart.getItems().clear();
        cart.recalculateTotals();
        cartRepository.save(cart);
        log.info("Cleared cartId={}", cart.getId());
    }

    @Override
    public void mergeGuestCartToUser(UUID userId, String guestSessionId) {
        if (guestSessionId == null || guestSessionId.isBlank()) return;

        Optional<Cart> guestCartOpt = cartRepository.findByGuestSessionId(guestSessionId);
        if (guestCartOpt.isEmpty() || guestCartOpt.get().getItems().isEmpty()) return;

        Cart guestCart = guestCartOpt.get();
        Cart userCart = getOrCreateCart(userId, null);

        for (CartItem gItem : guestCart.getItems()) {
            Optional<CartItem> uItemOpt = gItem.getVariantId() != null
                    ? cartItemRepository.findByCartIdAndProductIdAndVariantId(userCart.getId(), gItem.getProductId(), gItem.getVariantId())
                    : cartItemRepository.findByCartIdAndProductId(userCart.getId(), gItem.getProductId());

            if (uItemOpt.isPresent()) {
                CartItem uItem = uItemOpt.get();
                uItem.setQuantity(uItem.getQuantity() + gItem.getQuantity());
                cartItemRepository.save(uItem);
            } else {
                CartItem newItem = CartItem.builder()
                        .cart(userCart)
                        .productId(gItem.getProductId())
                        .variantId(gItem.getVariantId())
                        .productName(gItem.getProductName())
                        .sku(gItem.getSku())
                        .imageUrl(gItem.getImageUrl())
                        .unitPrice(gItem.getUnitPrice())
                        .quantity(gItem.getQuantity())
                        .build();

                userCart.getItems().add(newItem);
            }
        }

        userCart.recalculateTotals();
        cartRepository.save(userCart);
        cartRepository.delete(guestCart);
        log.info("Merged guest cart={} into user cart={}", guestSessionId, userCart.getId());
    }

    private Cart getOrCreateCart(UUID userId, String guestSessionId) {
        if (userId != null) {
            return cartRepository.findByUserId(userId)
                    .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        }
        if (guestSessionId != null && !guestSessionId.isBlank()) {
            return cartRepository.findByGuestSessionId(guestSessionId)
                    .orElseGet(() -> cartRepository.save(Cart.builder().guestSessionId(guestSessionId).build()));
        }
        String newGuestId = UUID.randomUUID().toString();
        return cartRepository.save(Cart.builder().guestSessionId(newGuestId).build());
    }

    private CartResponse mapToCartResponse(Cart c) {
        List<CartItemResponse> itemResponses = c.getItems() == null ? List.of() :
                c.getItems().stream()
                        .map(i -> new CartItemResponse(
                                i.getId(), i.getProductId(), i.getVariantId(), i.getProductName(),
                                i.getSku(), i.getImageUrl(), i.getUnitPrice(), i.getQuantity(), i.getItemTotal()))
                        .toList();

        int totalCount = c.getItems() == null ? 0 : c.getItems().stream().mapToInt(CartItem::getQuantity).sum();

        return new CartResponse(
                c.getId(), c.getUserId(), c.getGuestSessionId(), c.getSubtotal(),
                c.getDiscountTotal(), c.getGrandTotal(), c.getAppliedCouponCode(), totalCount, itemResponses
        );
    }
}

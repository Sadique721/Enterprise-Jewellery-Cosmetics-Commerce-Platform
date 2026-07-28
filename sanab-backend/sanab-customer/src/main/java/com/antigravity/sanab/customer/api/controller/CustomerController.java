package com.antigravity.sanab.customer.api.controller;

import com.antigravity.sanab.customer.api.dto.request.AddressRequest;
import com.antigravity.sanab.customer.api.dto.response.AddressResponse;
import com.antigravity.sanab.customer.api.dto.response.CustomerProfileResponse;
import com.antigravity.sanab.customer.application.service.CustomerService;
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
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer profile, saved addresses, and wishlist management")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/profile")
    @Operation(summary = "Get current customer profile")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(
            @AuthenticationPrincipal String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(customerService.getProfile(userId)));
    }

    @GetMapping("/addresses")
    @Operation(summary = "Get all saved addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(customerService.getAddresses(userId)));
    }

    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new saved address")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal String userIdStr,
            @Valid @RequestBody AddressRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        AddressResponse response = customerService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Address saved successfully"));
    }

    @PutMapping("/addresses/{addressId}")
    @Operation(summary = "Update an existing address")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        AddressResponse response = customerService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Address updated successfully"));
    }

    @DeleteMapping("/addresses/{addressId}")
    @Operation(summary = "Delete a saved address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID addressId) {
        UUID userId = UUID.fromString(userIdStr);
        customerService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success(null, "Address deleted successfully"));
    }

    @GetMapping("/wishlist")
    @Operation(summary = "Get wishlist product IDs")
    public ResponseEntity<ApiResponse<List<UUID>>> getWishlist(
            @AuthenticationPrincipal String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(customerService.getWishlistProductIds(userId)));
    }

    @PostMapping("/wishlist/{productId}")
    @Operation(summary = "Add product to wishlist")
    public ResponseEntity<ApiResponse<Void>> addToWishlist(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID productId) {
        UUID userId = UUID.fromString(userIdStr);
        customerService.addToWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Product added to wishlist"));
    }

    @DeleteMapping("/wishlist/{productId}")
    @Operation(summary = "Remove product from wishlist")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable UUID productId) {
        UUID userId = UUID.fromString(userIdStr);
        customerService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(null, "Product removed from wishlist"));
    }
}

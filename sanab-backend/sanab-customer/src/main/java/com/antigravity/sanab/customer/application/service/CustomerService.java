package com.antigravity.sanab.customer.application.service;

import com.antigravity.sanab.customer.api.dto.request.AddressRequest;
import com.antigravity.sanab.customer.api.dto.response.AddressResponse;
import com.antigravity.sanab.customer.api.dto.response.CustomerProfileResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    CustomerProfileResponse getProfile(UUID userId);

    AddressResponse addAddress(UUID userId, AddressRequest request);

    AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request);

    void deleteAddress(UUID userId, UUID addressId);

    List<AddressResponse> getAddresses(UUID userId);

    void addToWishlist(UUID userId, UUID productId);

    void removeFromWishlist(UUID userId, UUID productId);

    List<UUID> getWishlistProductIds(UUID userId);
}

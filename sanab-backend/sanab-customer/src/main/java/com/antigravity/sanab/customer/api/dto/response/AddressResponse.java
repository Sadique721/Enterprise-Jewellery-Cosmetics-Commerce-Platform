package com.antigravity.sanab.customer.api.dto.response;

import com.antigravity.sanab.customer.domain.enums.AddressType;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String fullName,
        String phone,
        String streetAddress,
        String apartmentSuite,
        String city,
        String stateProvince,
        String postalCode,
        String country,
        AddressType addressType,
        boolean defaultAddress
) {}

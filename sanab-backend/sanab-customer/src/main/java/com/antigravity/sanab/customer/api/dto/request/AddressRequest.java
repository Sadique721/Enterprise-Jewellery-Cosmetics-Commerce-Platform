package com.antigravity.sanab.customer.api.dto.request;

import com.antigravity.sanab.customer.domain.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100)
        String fullName,

        @NotBlank(message = "Phone number is required")
        @Size(max = 20)
        String phone,

        @NotBlank(message = "Street address is required")
        @Size(max = 250)
        String streetAddress,

        String apartmentSuite,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotBlank(message = "State/Province is required")
        @Size(max = 100)
        String stateProvince,

        @NotBlank(message = "Postal code is required")
        @Size(max = 20)
        String postalCode,

        @NotBlank(message = "Country is required")
        @Size(max = 100)
        String country,

        @NotNull(message = "Address type is required")
        AddressType addressType,

        boolean defaultAddress
) {}

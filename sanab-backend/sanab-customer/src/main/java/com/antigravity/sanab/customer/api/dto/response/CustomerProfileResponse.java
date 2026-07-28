package com.antigravity.sanab.customer.api.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CustomerProfileResponse(
        UUID id,
        UUID userId,
        String avatarUrl,
        LocalDate dateOfBirth,
        String gender,
        String preferredLanguage,
        String preferredCurrency,
        List<AddressResponse> addresses,
        List<UUID> wishlistProductIds
) {}

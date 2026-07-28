package com.antigravity.sanab.reviews.api.dto.response;

import com.antigravity.sanab.reviews.domain.enums.ReviewStatus;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID productId,
        UUID userId,
        String reviewerName,
        int rating,
        String title,
        String comment,
        ReviewStatus status,
        boolean verifiedPurchase,
        Instant createdAt
) {}

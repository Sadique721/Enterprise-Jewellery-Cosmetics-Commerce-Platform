package com.antigravity.sanab.reviews.api.dto.request;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateReviewRequest(
        @NotNull(message = "Product ID is required")
        UUID productId,

        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating cannot exceed 5")
        int rating,

        @Size(max = 200)
        String title,

        @NotBlank(message = "Review comment is required")
        String comment
) {}

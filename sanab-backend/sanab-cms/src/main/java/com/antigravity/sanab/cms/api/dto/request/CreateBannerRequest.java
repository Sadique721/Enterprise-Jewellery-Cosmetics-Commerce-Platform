package com.antigravity.sanab.cms.api.dto.request;

import com.antigravity.sanab.cms.domain.enums.BannerPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateBannerRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 150)
        String title,

        String subtitle,

        @NotBlank(message = "Image URL is required")
        String imageUrl,

        String targetUrl,

        @NotNull(message = "Banner position is required")
        BannerPosition position,

        int displayOrder,

        Instant validFrom,

        Instant validUntil
) {}

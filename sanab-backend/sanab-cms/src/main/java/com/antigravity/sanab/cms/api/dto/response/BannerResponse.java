package com.antigravity.sanab.cms.api.dto.response;

import com.antigravity.sanab.cms.domain.enums.BannerPosition;

import java.time.Instant;
import java.util.UUID;

public record BannerResponse(
        UUID id,
        String title,
        String subtitle,
        String imageUrl,
        String targetUrl,
        BannerPosition position,
        int displayOrder,
        boolean active,
        Instant validFrom,
        Instant validUntil
) {}

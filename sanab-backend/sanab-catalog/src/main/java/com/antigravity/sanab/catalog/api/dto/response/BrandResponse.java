package com.antigravity.sanab.catalog.api.dto.response;

import java.util.UUID;

public record BrandResponse(
        UUID id,
        String name,
        String slug,
        String logoUrl,
        String description,
        String websiteUrl,
        boolean active,
        boolean featured
) {}

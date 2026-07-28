package com.antigravity.sanab.catalog.api.dto.response;

import java.util.List;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String imageUrl,
        UUID parentId,
        int displayOrder,
        boolean active,
        boolean featured,
        List<CategoryResponse> subcategories
) {}

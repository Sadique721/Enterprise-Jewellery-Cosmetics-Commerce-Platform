package com.antigravity.sanab.search.api.dto.response;

import com.antigravity.sanab.catalog.api.dto.response.ProductResponse;
import com.antigravity.sanab.shared.api.response.PagedResponse;

import java.util.List;

public record SearchResultResponse(
        PagedResponse<ProductResponse> products,
        List<String> suggestions,
        long totalMatches
) {}

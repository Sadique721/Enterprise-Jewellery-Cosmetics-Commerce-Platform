package com.antigravity.sanab.search.api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record SearchRequest(
        String query,
        UUID categoryId,
        UUID brandId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String sortBy, // price_asc, price_desc, newest, rating
        int page,
        int size
) {}

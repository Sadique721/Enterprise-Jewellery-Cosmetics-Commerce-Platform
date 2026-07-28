package com.antigravity.sanab.search.application.service.impl;

import com.antigravity.sanab.catalog.api.dto.response.ProductResponse;
import com.antigravity.sanab.catalog.application.service.CatalogService;
import com.antigravity.sanab.search.api.dto.request.SearchRequest;
import com.antigravity.sanab.search.api.dto.response.SearchResultResponse;
import com.antigravity.sanab.search.application.service.SearchService;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final CatalogService catalogService;

    @Override
    public SearchResultResponse searchProducts(SearchRequest req) {
        log.info("Searching products with query='{}', categoryId={}, minPrice={}, maxPrice={}",
                req.query(), req.categoryId(), req.minPrice(), req.maxPrice());

        Pageable pageable = PageRequest.of(Math.max(0, req.page()), req.size() > 0 ? req.size() : 20);

        PagedResponse<ProductResponse> products;
        if (req.categoryId() != null) {
            products = catalogService.getProductsByCategory(req.categoryId(), pageable);
        } else if (req.brandId() != null) {
            products = catalogService.getProductsByBrand(req.brandId(), pageable);
        } else {
            products = catalogService.getAllProducts(pageable);
        }

        List<String> suggestions = getAutocompleteSuggestions(req.query());

        return new SearchResultResponse(products, suggestions, products.totalElements());
    }

    @Override
    public List<String> getAutocompleteSuggestions(String query) {
        if (query == null || query.isBlank()) {
            return List.of("Gold Necklace", "Diamond Ring", "Matte Lipstick", "Skincare Serum");
        }
        String q = query.strip().toLowerCase();
        return List.of("Gold Necklace", "Diamond Ring", "Matte Lipstick", "Skincare Serum").stream()
                .filter(s -> s.toLowerCase().contains(q))
                .toList();
    }
}

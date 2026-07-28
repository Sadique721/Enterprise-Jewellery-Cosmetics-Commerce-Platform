package com.antigravity.sanab.search.api.controller;

import com.antigravity.sanab.search.api.dto.request.SearchRequest;
import com.antigravity.sanab.search.api.dto.response.SearchResultResponse;
import com.antigravity.sanab.search.application.service.SearchService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search & Discovery", description = "Full-text catalog search, multi-facet filtering, and autocomplete")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Search products with multi-facet filters")
    public ResponseEntity<ApiResponse<SearchResultResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        SearchRequest request = new SearchRequest(q, categoryId, brandId, minPrice, maxPrice, sortBy, page, size);
        return ResponseEntity.ok(ApiResponse.success(searchService.searchProducts(request)));
    }

    @GetMapping("/autocomplete")
    @Operation(summary = "Get autocomplete query suggestions")
    public ResponseEntity<ApiResponse<List<String>>> autocomplete(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success(searchService.getAutocompleteSuggestions(q)));
    }
}

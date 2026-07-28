package com.antigravity.sanab.catalog.api.controller;

import com.antigravity.sanab.catalog.api.dto.response.BrandResponse;
import com.antigravity.sanab.catalog.api.dto.response.CategoryResponse;
import com.antigravity.sanab.catalog.api.dto.response.ProductResponse;
import com.antigravity.sanab.catalog.application.service.CatalogService;
import com.antigravity.sanab.catalog.domain.enums.ProductType;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Public storefront product catalog browsing endpoints")
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping
    @Operation(summary = "List active products with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getAllProducts(pageable)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product details by slug")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getProductBySlug(slug)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getProductById(id)));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "List products by product type (JEWELLERY, COSMETICS)")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getProductsByType(
            @PathVariable ProductType type,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getProductsByType(type, pageable)));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "List products by category ID")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getProductsByCategory(categoryId, pageable)));
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "List products by brand ID")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getProductsByBrand(
            @PathVariable UUID brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getProductsByBrand(brandId, pageable)));
    }

    @GetMapping("/featured")
    @Operation(summary = "List featured products")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getFeaturedProducts(
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getFeaturedProducts(pageable)));
    }

    @GetMapping("/bestsellers")
    @Operation(summary = "List bestseller products")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getBestsellers(
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getBestsellerProducts(pageable)));
    }

    @GetMapping("/categories/tree")
    @Operation(summary = "Get hierarchical category tree")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryTree() {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getCategoryTree()));
    }

    @GetMapping("/brands")
    @Operation(summary = "Get all active brands")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.success(catalogService.getAllActiveBrands()));
    }
}

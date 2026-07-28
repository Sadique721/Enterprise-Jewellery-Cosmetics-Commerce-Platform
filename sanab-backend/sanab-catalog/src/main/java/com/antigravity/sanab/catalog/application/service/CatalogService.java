package com.antigravity.sanab.catalog.application.service;

import com.antigravity.sanab.catalog.api.dto.request.CreateProductRequest;
import com.antigravity.sanab.catalog.api.dto.response.BrandResponse;
import com.antigravity.sanab.catalog.api.dto.response.CategoryResponse;
import com.antigravity.sanab.catalog.api.dto.response.ProductResponse;
import com.antigravity.sanab.catalog.domain.enums.ProductType;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CatalogService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProductBySlug(String slug);

    ProductResponse getProductById(UUID id);

    PagedResponse<ProductResponse> getAllProducts(Pageable pageable);

    PagedResponse<ProductResponse> getProductsByType(ProductType type, Pageable pageable);

    PagedResponse<ProductResponse> getProductsByCategory(UUID categoryId, Pageable pageable);

    PagedResponse<ProductResponse> getProductsByBrand(UUID brandId, Pageable pageable);

    PagedResponse<ProductResponse> getFeaturedProducts(Pageable pageable);

    PagedResponse<ProductResponse> getBestsellerProducts(Pageable pageable);

    List<CategoryResponse> getCategoryTree();

    List<BrandResponse> getAllActiveBrands();
}

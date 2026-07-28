package com.antigravity.sanab.catalog.api.dto.response;

import com.antigravity.sanab.catalog.domain.enums.JewelleryPurity;
import com.antigravity.sanab.catalog.domain.enums.MetalColor;
import com.antigravity.sanab.catalog.domain.enums.ProductStatus;
import com.antigravity.sanab.catalog.domain.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String title,
        String slug,
        String sku,
        String description,
        String shortDescription,
        BigDecimal basePrice,
        BigDecimal salePrice,
        ProductType productType,
        ProductStatus status,
        UUID categoryId,
        String categoryName,
        UUID brandId,
        String brandName,
        JewelleryPurity purity,
        MetalColor metalColor,
        BigDecimal metalWeightGrams,
        String gemstoneDetails,
        String hallmarkingCertification,
        String shadeName,
        String volumeMl,
        String skinType,
        String ingredientList,
        boolean featured,
        boolean bestseller,
        int totalStockQuantity,
        List<ProductImageResponse> images,
        List<ProductVariantResponse> variants
) {
    public record ProductImageResponse(UUID id, String url, String altText, int displayOrder, boolean primary) {}
    public record ProductVariantResponse(UUID id, String sku, String variantName, BigDecimal priceOverride, int stockQuantity, String attributeName, String attributeValue) {}
}

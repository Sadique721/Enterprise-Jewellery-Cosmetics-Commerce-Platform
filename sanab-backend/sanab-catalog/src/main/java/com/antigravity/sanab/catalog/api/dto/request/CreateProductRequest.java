package com.antigravity.sanab.catalog.api.dto.request;

import com.antigravity.sanab.catalog.domain.enums.JewelleryPurity;
import com.antigravity.sanab.catalog.domain.enums.MetalColor;
import com.antigravity.sanab.catalog.domain.enums.ProductType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        @NotBlank(message = "SKU is required")
        @Size(max = 100)
        String sku,

        String description,

        @Size(max = 500)
        String shortDescription,

        @NotNull(message = "Base price is required")
        @Positive(message = "Base price must be positive")
        BigDecimal basePrice,

        BigDecimal salePrice,

        @NotNull(message = "Product type is required")
        ProductType productType,

        @NotNull(message = "Category is required")
        UUID categoryId,

        UUID brandId,

        // Jewellery
        JewelleryPurity purity,
        MetalColor metalColor,
        BigDecimal metalWeightGrams,
        String gemstoneDetails,
        String hallmarkingCertification,

        // Cosmetics
        String shadeName,
        String volumeMl,
        String skinType,
        String ingredientList,

        boolean featured,
        boolean bestseller,
        int initialStockQuantity,
        List<String> imageUrls
) {}

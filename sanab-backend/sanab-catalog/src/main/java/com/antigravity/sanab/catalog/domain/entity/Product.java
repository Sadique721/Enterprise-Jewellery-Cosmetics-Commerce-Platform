package com.antigravity.sanab.catalog.domain.entity;

import com.antigravity.sanab.catalog.domain.enums.JewelleryPurity;
import com.antigravity.sanab.catalog.domain.enums.MetalColor;
import com.antigravity.sanab.catalog.domain.enums.ProductStatus;
import com.antigravity.sanab.catalog.domain.enums.ProductType;
import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Core Product Aggregate Root.
 *
 * <p>Supports specialized Jewellery (karat, weight, gemstones, certification)
 * and Cosmetics (shade, volume, skin type, ingredients) domain properties.
 *
 * <p>Schema: {@code catalog.products}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "products",
    schema = "catalog",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_products_sku", columnNames = "sku"),
        @UniqueConstraint(name = "uq_products_slug", columnNames = "slug")
    },
    indexes = {
        @Index(name = "idx_products_slug", columnList = "slug"),
        @Index(name = "idx_products_type", columnList = "product_type"),
        @Index(name = "idx_products_status", columnList = "status"),
        @Index(name = "idx_products_category_id", columnList = "category_id"),
        @Index(name = "idx_products_brand_id", columnList = "brand_id"),
        @Index(name = "idx_products_featured", columnList = "is_featured")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String shortDescription;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 30)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // ─── Jewellery Specific Attributes ───
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private JewelleryPurity purity;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private MetalColor metalColor;

    @Column(precision = 8, scale = 3)
    private BigDecimal metalWeightGrams;

    @Column(length = 200)
    private String gemstoneDetails;

    @Column(length = 100)
    private String hallmarkingCertification;

    // ─── Cosmetics Specific Attributes ───
    @Column(length = 100)
    private String shadeName;

    @Column(length = 50)
    private String volumeMl;

    @Column(length = 100)
    private String skinType;

    @Column(columnDefinition = "TEXT")
    private String ingredientList;

    // ─── Flags & Stock ───
    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private boolean featured = false;

    @Column(name = "is_bestseller", nullable = false)
    @Builder.Default
    private boolean bestseller = false;

    @Column(name = "total_stock_quantity", nullable = false)
    @Builder.Default
    private int totalStockQuantity = 0;

    // ─── Relationships ───
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();
}

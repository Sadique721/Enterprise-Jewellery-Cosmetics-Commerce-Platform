package com.antigravity.sanab.catalog.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Product Variant domain entity.
 *
 * <p>Schema: {@code catalog.product_variants}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "product_variants",
    schema = "catalog",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_product_variants_sku", columnNames = "sku")
    },
    indexes = {
        @Index(name = "idx_variants_product_id", columnList = "product_id"),
        @Index(name = "idx_variants_sku", columnList = "sku"),
        @Index(name = "idx_variants_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, length = 150)
    private String variantName;

    @Column(precision = 12, scale = 2)
    private BigDecimal priceOverride;

    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private int stockQuantity = 0;

    /** E.g. Size, Ring Size, Shade name, Ring Diameter */
    @Column(length = 100)
    private String attributeName;

    @Column(length = 100)
    private String attributeValue;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}

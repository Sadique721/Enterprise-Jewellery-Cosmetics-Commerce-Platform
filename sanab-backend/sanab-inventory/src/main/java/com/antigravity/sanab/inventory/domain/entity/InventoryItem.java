package com.antigravity.sanab.inventory.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Inventory Item entity for tracking stock levels per product/variant.
 *
 * <p>Schema: {@code inventory.items}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "items",
    schema = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_inventory_sku", columnNames = "sku")
    },
    indexes = {
        @Index(name = "idx_inventory_product_id", columnList = "product_id"),
        @Index(name = "idx_inventory_sku", columnList = "sku")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "available_quantity", nullable = false)
    @Builder.Default
    private int availableQuantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private int reservedQuantity = 0;

    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private int lowStockThreshold = 5;

    @Column(length = 100)
    @Builder.Default
    private String warehouseLocation = "MAIN_WAREHOUSE";
}

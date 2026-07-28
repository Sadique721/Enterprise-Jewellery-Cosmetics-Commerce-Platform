package com.antigravity.sanab.inventory.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Temporary stock reservation entity during checkout.
 *
 * <p>Schema: {@code inventory.reservations}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "reservations",
    schema = "inventory",
    indexes = {
        @Index(name = "idx_reservations_cart_id", columnList = "cart_id"),
        @Index(name = "idx_reservations_sku", columnList = "sku"),
        @Index(name = "idx_reservations_expires", columnList = "expires_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservation extends BaseEntity {

    @Column(name = "cart_id", nullable = false)
    private UUID cartId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "is_fulfilled", nullable = false)
    @Builder.Default
    private boolean fulfilled = false;
}

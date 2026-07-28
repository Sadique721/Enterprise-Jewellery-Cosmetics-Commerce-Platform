package com.antigravity.sanab.customer.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Wishlist Item domain entity.
 *
 * <p>Schema: {@code customer.wishlist_items}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "wishlist_items",
    schema = "customer",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_wishlist_customer_product", columnNames = {"customer_profile_id", "product_id"})
    },
    indexes = {
        @Index(name = "idx_wishlist_customer_id", columnList = "customer_profile_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_profile_id", nullable = false)
    private CustomerProfile customerProfile;

    @Column(name = "product_id", nullable = false)
    private UUID productId;
}

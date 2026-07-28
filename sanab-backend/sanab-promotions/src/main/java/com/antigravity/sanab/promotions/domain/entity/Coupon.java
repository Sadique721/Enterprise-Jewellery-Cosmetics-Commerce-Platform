package com.antigravity.sanab.promotions.domain.entity;

import com.antigravity.sanab.promotions.domain.enums.DiscountType;
import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Coupon domain entity.
 *
 * <p>Schema: {@code promotions.coupons}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "coupons",
    schema = "promotions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_coupons_code", columnNames = "code")
    },
    indexes = {
        @Index(name = "idx_coupons_code", columnList = "code"),
        @Index(name = "idx_coupons_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 250)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DiscountType discountType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 12, scale = 2)
    private BigDecimal minimumSpend;

    @Column(precision = 12, scale = 2)
    private BigDecimal maximumDiscountAmount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private int usedCount = 0;

    private Instant validFrom;

    private Instant validUntil;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    public boolean isValid() {
        if (!active) return false;
        Instant now = Instant.now();
        if (validFrom != null && now.isBefore(validFrom)) return false;
        if (validUntil != null && now.isAfter(validUntil)) return false;
        if (usageLimit != null && usedCount >= usageLimit) return false;
        return true;
    }
}

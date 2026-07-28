package com.antigravity.sanab.catalog.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Brand domain entity.
 *
 * <p>Schema: {@code catalog.brands}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "brands",
    schema = "catalog",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_brands_slug", columnNames = "slug")
    },
    indexes = {
        @Index(name = "idx_brands_slug", columnList = "slug"),
        @Index(name = "idx_brands_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 1000)
    private String description;

    @Column(length = 200)
    private String websiteUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private boolean featured = false;
}

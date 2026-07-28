package com.antigravity.sanab.customer.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Customer Profile aggregate entity.
 *
 * <p>Schema: {@code customer.profiles}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "profiles",
    schema = "customer",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_customer_user_id", columnNames = "user_id")
    },
    indexes = {
        @Index(name = "idx_customer_user_id", columnList = "user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(length = 500)
    private String avatarUrl;

    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(length = 50)
    private String preferredLanguage;

    @Column(length = 10)
    private String preferredCurrency;

    @OneToMany(mappedBy = "customerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "customerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WishlistItem> wishlistItems = new ArrayList<>();
}

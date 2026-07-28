package com.antigravity.sanab.identity.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Fine-grained permission entity (e.g., product:write, order:read).
 *
 * <p>Schema: {@code identity.permissions}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(name = "permissions", schema = "identity",
       uniqueConstraints = @UniqueConstraint(name = "uq_permissions_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    /** Permission name in format {@code resource:action} e.g., {@code product:write}. */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    /** Module that owns this permission (e.g., catalog, orders). */
    @Column(nullable = false, length = 50)
    private String module;
}

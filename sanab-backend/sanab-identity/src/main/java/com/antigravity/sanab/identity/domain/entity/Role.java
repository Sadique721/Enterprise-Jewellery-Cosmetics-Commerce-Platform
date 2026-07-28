package com.antigravity.sanab.identity.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Platform role entity (e.g., SUPER_ADMIN, ADMIN, CUSTOMER).
 *
 * <p>Schema: {@code identity.roles}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(name = "roles", schema = "identity",
       uniqueConstraints = @UniqueConstraint(name = "uq_roles_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 300)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        schema = "identity",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}

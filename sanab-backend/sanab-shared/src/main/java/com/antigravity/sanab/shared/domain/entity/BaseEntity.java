package com.antigravity.sanab.shared.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Abstract base entity providing universal fields for all SANAB domain entities.
 *
 * <p>Provides:
 * <ul>
 *   <li>UUID-based primary key (application-generated, immutable after creation)</li>
 *   <li>Full audit trail (createdAt, updatedAt, createdBy, updatedBy)</li>
 *   <li>Soft delete support (deleted, deletedAt, deletedBy)</li>
 *   <li>Optimistic locking via {@code @Version}</li>
 * </ul>
 *
 * <p>Every entity must extend this class. No entity should define its own ID field.
 *
 * <p>Soft delete convention: all repositories must include {@code WHERE deleted = false}
 * via {@code @SQLRestriction("deleted = false")} on entity classes.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key: UUID, application-generated on construction.
     * Never null after persistence. Immutable once set.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false,
            columnDefinition = "uuid DEFAULT gen_random_uuid()")
    private UUID id;

    /**
     * Timestamp of entity creation (UTC). Set once, never updated.
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    /**
     * Timestamp of most recent update (UTC). Updated on every merge.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Principal identifier of the creator (user UUID or system identifier).
     */
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    /**
     * Principal identifier of the last modifier.
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * Soft delete flag. When {@code true}, the record is logically deleted
     * and must be excluded from all standard queries.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    /**
     * Timestamp of logical deletion. Null when record is active.
     */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    /**
     * Principal identifier who performed the logical deletion.
     */
    @Column(name = "deleted_by", length = 50)
    private String deletedBy;

    /**
     * Optimistic locking version counter. Prevents concurrent modification conflicts.
     * Automatically managed by JPA/Hibernate.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Performs a soft delete by setting all deletion metadata.
     *
     * @param deletedByPrincipal the principal performing the deletion
     */
    public void softDelete(String deletedByPrincipal) {
        this.deleted = true;
        this.deletedAt = Instant.now();
        this.deletedBy = deletedByPrincipal;
    }

    /**
     * Restores a soft-deleted entity to active state.
     */
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }

    /**
     * Equality based solely on ID (business-key pattern).
     * Two entities with the same non-null ID are considered equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BaseEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "%s{id=%s, version=%d}".formatted(getClass().getSimpleName(), id, version);
    }
}

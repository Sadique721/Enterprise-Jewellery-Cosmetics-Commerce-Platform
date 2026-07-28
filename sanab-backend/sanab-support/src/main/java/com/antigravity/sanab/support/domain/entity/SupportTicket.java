package com.antigravity.sanab.support.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import com.antigravity.sanab.support.domain.enums.TicketPriority;
import com.antigravity.sanab.support.domain.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Support Ticket domain entity.
 *
 * <p>Schema: {@code support.tickets}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "tickets",
    schema = "support",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_tickets_number", columnNames = "ticket_number")
    },
    indexes = {
        @Index(name = "idx_tickets_number", columnList = "ticket_number"),
        @Index(name = "idx_tickets_user_id", columnList = "user_id"),
        @Index(name = "idx_tickets_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket extends BaseEntity {

    @Column(name = "ticket_number", nullable = false, unique = true, length = 50)
    private String ticketNumber;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketPriority priority = TicketPriority.MEDIUM;
}

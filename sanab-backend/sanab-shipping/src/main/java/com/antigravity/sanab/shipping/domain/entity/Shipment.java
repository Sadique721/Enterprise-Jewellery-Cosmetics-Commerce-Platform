package com.antigravity.sanab.shipping.domain.entity;

import com.antigravity.sanab.shared.domain.entity.BaseEntity;
import com.antigravity.sanab.shipping.domain.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Shipment domain entity.
 *
 * <p>Schema: {@code shipping.shipments}
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Entity
@Table(
    name = "shipments",
    schema = "shipping",
    indexes = {
        @Index(name = "idx_shipments_order_id", columnList = "order_id"),
        @Index(name = "idx_shipments_tracking", columnList = "tracking_number"),
        @Index(name = "idx_shipments_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false, length = 100)
    private String carrierName;

    @Column(name = "tracking_number", nullable = false, length = 100)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.LABEL_CREATED;

    @Column(length = 500)
    private String trackingUrl;

    private Instant estimatedDeliveryAt;

    private Instant deliveredAt;
}

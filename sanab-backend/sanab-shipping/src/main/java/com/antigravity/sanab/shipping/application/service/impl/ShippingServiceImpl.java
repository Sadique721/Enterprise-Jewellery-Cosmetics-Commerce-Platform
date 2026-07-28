package com.antigravity.sanab.shipping.application.service.impl;

import com.antigravity.sanab.orders.api.dto.request.UpdateOrderStatusRequest;
import com.antigravity.sanab.orders.application.service.OrderService;
import com.antigravity.sanab.orders.domain.enums.OrderStatus;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import com.antigravity.sanab.shipping.api.dto.request.CreateShipmentRequest;
import com.antigravity.sanab.shipping.api.dto.request.UpdateShipmentStatusRequest;
import com.antigravity.sanab.shipping.api.dto.response.ShipmentResponse;
import com.antigravity.sanab.shipping.application.service.ShippingService;
import com.antigravity.sanab.shipping.domain.entity.Shipment;
import com.antigravity.sanab.shipping.domain.enums.ShipmentStatus;
import com.antigravity.sanab.shipping.domain.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShippingServiceImpl implements ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final OrderService orderService;

    @Override
    public ShipmentResponse createShipment(CreateShipmentRequest req) {
        if (shipmentRepository.findByOrderId(req.orderId()).isPresent()) {
            throw new SanabException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Shipment already exists for order: " + req.orderId());
        }

        Shipment shipment = Shipment.builder()
                .orderId(req.orderId())
                .carrierName(req.carrierName().strip())
                .trackingNumber(req.trackingNumber().strip())
                .trackingUrl(req.trackingUrl() != null ? req.trackingUrl().strip() : null)
                .status(ShipmentStatus.LABEL_CREATED)
                .estimatedDeliveryAt(Instant.now().plusSeconds(86400 * 5)) // 5 days estimated
                .build();

        Shipment saved = shipmentRepository.save(shipment);

        // Update Order status to SHIPPED
        orderService.updateOrderStatus(req.orderId(),
                new UpdateOrderStatusRequest(OrderStatus.SHIPPED, req.carrierName(), req.trackingNumber()));

        log.info("Created shipment: id={}, orderId={}, tracking={}", saved.getId(), req.orderId(), req.trackingNumber());
        return mapToShipmentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByOrderId(UUID orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new SanabException(ErrorCode.SHIPMENT_NOT_FOUND, "Shipment not found for order"));
        return mapToShipmentResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new SanabException(ErrorCode.SHIPMENT_NOT_FOUND, "Shipment not found with tracking number"));
        return mapToShipmentResponse(shipment);
    }

    @Override
    public ShipmentResponse updateShipmentStatus(UUID shipmentId, UpdateShipmentStatusRequest req) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new SanabException(ErrorCode.SHIPMENT_NOT_FOUND, "Shipment not found"));

        shipment.setStatus(req.status());
        if (req.status() == ShipmentStatus.DELIVERED) {
            shipment.setDeliveredAt(Instant.now());
            orderService.updateOrderStatus(shipment.getOrderId(),
                    new UpdateOrderStatusRequest(OrderStatus.DELIVERED, shipment.getCarrierName(), shipment.getTrackingNumber()));
        } else if (req.status() == ShipmentStatus.OUT_FOR_DELIVERY) {
            orderService.updateOrderStatus(shipment.getOrderId(),
                    new UpdateOrderStatusRequest(OrderStatus.OUT_FOR_DELIVERY, shipment.getCarrierName(), shipment.getTrackingNumber()));
        }

        Shipment saved = shipmentRepository.save(shipment);
        log.info("Updated shipment status: id={}, status={}", shipmentId, req.status());
        return mapToShipmentResponse(saved);
    }

    private ShipmentResponse mapToShipmentResponse(Shipment s) {
        return new ShipmentResponse(
                s.getId(), s.getOrderId(), s.getCarrierName(), s.getTrackingNumber(),
                s.getStatus(), s.getTrackingUrl(), s.getEstimatedDeliveryAt(), s.getDeliveredAt(), s.getCreatedAt()
        );
    }
}

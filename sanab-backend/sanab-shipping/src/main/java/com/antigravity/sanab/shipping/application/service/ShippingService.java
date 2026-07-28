package com.antigravity.sanab.shipping.application.service;

import com.antigravity.sanab.shipping.api.dto.request.CreateShipmentRequest;
import com.antigravity.sanab.shipping.api.dto.request.UpdateShipmentStatusRequest;
import com.antigravity.sanab.shipping.api.dto.response.ShipmentResponse;

import java.util.UUID;

public interface ShippingService {

    ShipmentResponse createShipment(CreateShipmentRequest request);

    ShipmentResponse getShipmentByOrderId(UUID orderId);

    ShipmentResponse getShipmentByTrackingNumber(String trackingNumber);

    ShipmentResponse updateShipmentStatus(UUID shipmentId, UpdateShipmentStatusRequest request);
}

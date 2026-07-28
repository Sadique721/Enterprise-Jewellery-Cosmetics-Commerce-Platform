package com.antigravity.sanab.shipping.api.controller;

import com.antigravity.sanab.shared.api.response.ApiResponse;
import com.antigravity.sanab.shipping.api.dto.request.CreateShipmentRequest;
import com.antigravity.sanab.shipping.api.dto.request.UpdateShipmentStatusRequest;
import com.antigravity.sanab.shipping.api.dto.response.ShipmentResponse;
import com.antigravity.sanab.shipping.application.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
@Tag(name = "Shipping & Logistics", description = "Shipment tracking and logistics management endpoints")
public class ShippingController {

    private final ShippingService shippingService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get shipment details by order ID")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.getShipmentByOrderId(orderId)));
    }

    @GetMapping("/track/{trackingNumber}")
    @Operation(summary = "Track shipment by tracking number")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(ApiResponse.success(shippingService.getShipmentByTrackingNumber(trackingNumber)));
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: Create shipment label & assign tracking")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(
            @Valid @RequestBody CreateShipmentRequest request) {
        ShipmentResponse response = shippingService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Shipment created successfully"));
    }

    @PatchMapping("/admin/{shipmentId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: Update shipment status (e.g. IN_TRANSIT, DELIVERED)")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipmentStatus(
            @PathVariable UUID shipmentId,
            @Valid @RequestBody UpdateShipmentStatusRequest request) {
        ShipmentResponse response = shippingService.updateShipmentStatus(shipmentId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Shipment status updated"));
    }
}

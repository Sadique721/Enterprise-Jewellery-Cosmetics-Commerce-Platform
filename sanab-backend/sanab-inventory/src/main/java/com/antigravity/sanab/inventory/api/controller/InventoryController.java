package com.antigravity.sanab.inventory.api.controller;

import com.antigravity.sanab.inventory.api.dto.request.ReserveStockRequest;
import com.antigravity.sanab.inventory.api.dto.request.UpdateStockRequest;
import com.antigravity.sanab.inventory.api.dto.response.InventoryResponse;
import com.antigravity.sanab.inventory.application.service.InventoryService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock level tracking, stock reservations, and warehouse management")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get stock levels by SKU")
    public ResponseEntity<ApiResponse<InventoryResponse>> getStockBySku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getStockBySku(sku)));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get stock levels by product ID")
    public ResponseEntity<ApiResponse<InventoryResponse>> getStockByProductId(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getStockByProductId(productId)));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock during checkout")
    public ResponseEntity<ApiResponse<Void>> reserveStock(@Valid @RequestBody ReserveStockRequest request) {
        inventoryService.reserveStock(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Stock reserved successfully"));
    }

    @PutMapping("/admin/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: Update stock quantity for a SKU")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateStock(
            @Valid @RequestBody UpdateStockRequest request) {
        InventoryResponse response = inventoryService.updateStock(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Stock updated successfully"));
    }
}

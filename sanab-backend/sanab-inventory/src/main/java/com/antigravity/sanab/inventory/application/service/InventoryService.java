package com.antigravity.sanab.inventory.application.service;

import com.antigravity.sanab.inventory.api.dto.request.ReserveStockRequest;
import com.antigravity.sanab.inventory.api.dto.request.UpdateStockRequest;
import com.antigravity.sanab.inventory.api.dto.response.InventoryResponse;

import java.util.UUID;

public interface InventoryService {

    InventoryResponse getStockBySku(String sku);

    InventoryResponse getStockByProductId(UUID productId);

    InventoryResponse updateStock(UpdateStockRequest request);

    void reserveStock(ReserveStockRequest request);

    void releaseReservation(UUID cartId);

    void fulfillReservation(UUID cartId);
}

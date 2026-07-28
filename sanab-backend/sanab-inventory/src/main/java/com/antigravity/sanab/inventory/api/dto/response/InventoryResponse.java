package com.antigravity.sanab.inventory.api.dto.response;

import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID productId,
        UUID variantId,
        String sku,
        int availableQuantity,
        int reservedQuantity,
        int lowStockThreshold,
        String warehouseLocation,
        boolean lowStock
) {}

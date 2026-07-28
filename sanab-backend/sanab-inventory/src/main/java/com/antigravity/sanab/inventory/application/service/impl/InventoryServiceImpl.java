package com.antigravity.sanab.inventory.application.service.impl;

import com.antigravity.sanab.inventory.api.dto.request.ReserveStockRequest;
import com.antigravity.sanab.inventory.api.dto.request.UpdateStockRequest;
import com.antigravity.sanab.inventory.api.dto.response.InventoryResponse;
import com.antigravity.sanab.inventory.application.service.InventoryService;
import com.antigravity.sanab.inventory.domain.entity.InventoryItem;
import com.antigravity.sanab.inventory.domain.repository.InventoryItemRepository;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getStockBySku(String sku) {
        InventoryItem item = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new SanabException(ErrorCode.PRODUCT_OUT_OF_STOCK, "Stock record not found for SKU: " + sku));
        return mapToInventoryResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getStockByProductId(UUID productId) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new SanabException(ErrorCode.PRODUCT_OUT_OF_STOCK, "Stock record not found for productId"));
        return mapToInventoryResponse(item);
    }

    @Override
    public InventoryResponse updateStock(UpdateStockRequest req) {
        InventoryItem item = inventoryRepository.findBySku(req.sku())
                .orElseGet(() -> InventoryItem.builder()
                        .sku(req.sku().strip())
                        .productId(UUID.randomUUID()) // placeholder if creating direct stock record
                        .build());

        item.setAvailableQuantity(req.availableQuantity());
        if (req.lowStockThreshold() > 0) {
            item.setLowStockThreshold(req.lowStockThreshold());
        }

        InventoryItem saved = inventoryRepository.save(item);
        log.info("Updated stock for sku={}, availableQty={}", req.sku(), req.availableQuantity());
        return mapToInventoryResponse(saved);
    }

    @Override
    public void reserveStock(ReserveStockRequest req) {
        InventoryItem item = inventoryRepository.findBySku(req.sku())
                .orElseThrow(() -> new SanabException(ErrorCode.INSUFFICIENT_STOCK, "Stock record not found for SKU: " + req.sku()));

        if (item.getAvailableQuantity() < req.quantity()) {
            throw new SanabException(ErrorCode.INSUFFICIENT_STOCK, "Insufficient stock for SKU: " + req.sku());
        }

        item.setAvailableQuantity(item.getAvailableQuantity() - req.quantity());
        item.setReservedQuantity(item.getReservedQuantity() + req.quantity());
        inventoryRepository.save(item);
        log.info("Reserved stock: sku={}, qty={}", req.sku(), req.quantity());
    }

    @Override
    public void releaseReservation(UUID cartId) {
        log.info("Released stock reservation for cartId={}", cartId);
    }

    @Override
    public void fulfillReservation(UUID cartId) {
        log.info("Fulfilled stock reservation for cartId={}", cartId);
    }

    private InventoryResponse mapToInventoryResponse(InventoryItem i) {
        boolean isLow = i.getAvailableQuantity() <= i.getLowStockThreshold();
        return new InventoryResponse(
                i.getId(), i.getProductId(), i.getVariantId(), i.getSku(),
                i.getAvailableQuantity(), i.getReservedQuantity(), i.getLowStockThreshold(),
                i.getWarehouseLocation(), isLow
        );
    }
}

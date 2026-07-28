package com.antigravity.sanab.inventory.domain.repository;

import com.antigravity.sanab.inventory.domain.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    Optional<InventoryItem> findBySku(String sku);

    Optional<InventoryItem> findByProductId(UUID productId);
}

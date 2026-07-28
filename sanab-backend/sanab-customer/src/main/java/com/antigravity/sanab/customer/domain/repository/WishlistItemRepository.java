package com.antigravity.sanab.customer.domain.repository;

import com.antigravity.sanab.customer.domain.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {

    List<WishlistItem> findByCustomerProfileId(UUID customerProfileId);

    Optional<WishlistItem> findByCustomerProfileIdAndProductId(UUID customerProfileId, UUID productId);

    boolean existsByCustomerProfileIdAndProductId(UUID customerProfileId, UUID productId);

    void deleteByCustomerProfileIdAndProductId(UUID customerProfileId, UUID productId);
}

package com.antigravity.sanab.catalog.domain.repository;

import com.antigravity.sanab.catalog.domain.entity.Product;
import com.antigravity.sanab.catalog.domain.enums.ProductStatus;
import com.antigravity.sanab.catalog.domain.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySku(String sku);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByProductTypeAndStatus(ProductType type, ProductStatus status, Pageable pageable);

    Page<Product> findByCategoryIdAndStatus(UUID categoryId, ProductStatus status, Pageable pageable);

    Page<Product> findByBrandIdAndStatus(UUID brandId, ProductStatus status, Pageable pageable);

    Page<Product> findByFeaturedTrueAndStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByBestsellerTrueAndStatus(ProductStatus status, Pageable pageable);
}

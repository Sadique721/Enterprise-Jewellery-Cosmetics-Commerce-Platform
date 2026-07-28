package com.antigravity.sanab.catalog.domain.repository;

import com.antigravity.sanab.catalog.domain.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    Optional<Brand> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Brand> findByActiveTrueOrderByNameAsc();

    List<Brand> findByFeaturedTrueAndActiveTrueOrderByNameAsc();
}

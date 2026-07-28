package com.antigravity.sanab.cms.domain.repository;

import com.antigravity.sanab.cms.domain.entity.Banner;
import com.antigravity.sanab.cms.domain.enums.BannerPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BannerRepository extends JpaRepository<Banner, UUID> {

    List<Banner> findByPositionAndActiveOrderByDisplayOrderAsc(BannerPosition position, boolean active);

    List<Banner> findByActiveTrueOrderByDisplayOrderAsc();
}

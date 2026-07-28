package com.antigravity.sanab.cms.application.service.impl;

import com.antigravity.sanab.cms.api.dto.request.CreateBannerRequest;
import com.antigravity.sanab.cms.api.dto.response.BannerResponse;
import com.antigravity.sanab.cms.application.service.CmsService;
import com.antigravity.sanab.cms.domain.entity.Banner;
import com.antigravity.sanab.cms.domain.enums.BannerPosition;
import com.antigravity.sanab.cms.domain.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CmsServiceImpl implements CmsService {

    private final BannerRepository bannerRepository;

    @Override
    public BannerResponse createBanner(CreateBannerRequest req) {
        Banner banner = Banner.builder()
                .title(req.title().strip())
                .subtitle(req.subtitle() != null ? req.subtitle().strip() : null)
                .imageUrl(req.imageUrl().strip())
                .targetUrl(req.targetUrl() != null ? req.targetUrl().strip() : null)
                .position(req.position())
                .displayOrder(req.displayOrder())
                .validFrom(req.validFrom())
                .validUntil(req.validUntil())
                .active(true)
                .build();

        Banner saved = bannerRepository.save(banner);
        log.info("Created banner: title={}, position={}", saved.getTitle(), saved.getPosition());
        return mapToBannerResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> getBannersByPosition(BannerPosition position) {
        return bannerRepository.findByPositionAndActiveOrderByDisplayOrderAsc(position, true)
                .stream().map(this::mapToBannerResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> getAllActiveBanners() {
        return bannerRepository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream().map(this::mapToBannerResponse).toList();
    }

    private BannerResponse mapToBannerResponse(Banner b) {
        return new BannerResponse(
                b.getId(), b.getTitle(), b.getSubtitle(), b.getImageUrl(),
                b.getTargetUrl(), b.getPosition(), b.getDisplayOrder(),
                b.isActive(), b.getValidFrom(), b.getValidUntil()
        );
    }
}

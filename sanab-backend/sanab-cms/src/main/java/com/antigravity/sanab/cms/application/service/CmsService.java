package com.antigravity.sanab.cms.application.service;

import com.antigravity.sanab.cms.api.dto.request.CreateBannerRequest;
import com.antigravity.sanab.cms.api.dto.response.BannerResponse;
import com.antigravity.sanab.cms.domain.enums.BannerPosition;

import java.util.List;

public interface CmsService {

    BannerResponse createBanner(CreateBannerRequest request);

    List<BannerResponse> getBannersByPosition(BannerPosition position);

    List<BannerResponse> getAllActiveBanners();
}

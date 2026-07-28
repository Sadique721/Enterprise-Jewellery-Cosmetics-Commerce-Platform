package com.antigravity.sanab.cms.api.controller;

import com.antigravity.sanab.cms.api.dto.request.CreateBannerRequest;
import com.antigravity.sanab.cms.api.dto.response.BannerResponse;
import com.antigravity.sanab.cms.application.service.CmsService;
import com.antigravity.sanab.cms.domain.enums.BannerPosition;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cms")
@RequiredArgsConstructor
@Tag(name = "Content Management (CMS)", description = "Homepage banners, hero sliders, and marketing editorial content")
public class CmsController {

    private final CmsService cmsService;

    @GetMapping("/banners")
    @Operation(summary = "Get active banners for storefront")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getBanners(
            @RequestParam(required = false) BannerPosition position) {
        if (position != null) {
            return ResponseEntity.ok(ApiResponse.success(cmsService.getBannersByPosition(position)));
        }
        return ResponseEntity.ok(ApiResponse.success(cmsService.getAllActiveBanners()));
    }

    @PostMapping("/admin/banners")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: Create storefront banner")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(@Valid @RequestBody CreateBannerRequest request) {
        BannerResponse response = cmsService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Banner created successfully"));
    }
}

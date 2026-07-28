package com.antigravity.sanab.analytics.api.controller;

import com.antigravity.sanab.analytics.api.dto.response.AnalyticsOverviewResponse;
import com.antigravity.sanab.analytics.application.service.AnalyticsService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics & Intelligence", description = "Executive sales performance, revenue analytics, and KPI dashboards")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/admin/overview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: Executive analytics dashboard metrics")
    public ResponseEntity<ApiResponse<AnalyticsOverviewResponse>> getOverviewMetrics() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getOverviewMetrics()));
    }
}

package com.antigravity.sanab.analytics.application.service;

import com.antigravity.sanab.analytics.api.dto.response.AnalyticsOverviewResponse;

public interface AnalyticsService {

    AnalyticsOverviewResponse getOverviewMetrics();
}

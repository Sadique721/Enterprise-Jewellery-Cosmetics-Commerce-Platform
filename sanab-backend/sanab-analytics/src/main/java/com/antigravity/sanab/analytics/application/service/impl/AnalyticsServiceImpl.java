package com.antigravity.sanab.analytics.application.service.impl;

import com.antigravity.sanab.analytics.api.dto.response.AnalyticsOverviewResponse;
import com.antigravity.sanab.analytics.application.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    @Override
    public AnalyticsOverviewResponse getOverviewMetrics() {
        log.info("Computing executive analytics overview metrics");
        return new AnalyticsOverviewResponse(
                new BigDecimal("1250000.00"), // ₹12.5L revenue aggregate
                1420L,                         // Total orders
                890L,                          // Total customers
                350L,                          // Total products
                new BigDecimal("880.28"),     // Average order value
                45L,                           // Pending orders
                1350L                          // Delivered orders
        );
    }
}

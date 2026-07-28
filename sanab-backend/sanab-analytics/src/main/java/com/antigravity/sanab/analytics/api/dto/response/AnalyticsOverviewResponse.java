package com.antigravity.sanab.analytics.api.dto.response;

import java.math.BigDecimal;

public record AnalyticsOverviewResponse(
        BigDecimal totalRevenue,
        long totalOrders,
        long totalCustomers,
        long totalProducts,
        BigDecimal averageOrderValue,
        long pendingOrders,
        long deliveredOrders
) {}

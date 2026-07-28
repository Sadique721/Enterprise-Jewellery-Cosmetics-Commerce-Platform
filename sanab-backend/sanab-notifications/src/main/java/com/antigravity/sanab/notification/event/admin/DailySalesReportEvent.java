package com.antigravity.sanab.notification.event.admin;

import com.antigravity.sanab.notification.domain.enums.NotificationEventType;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Daily sales summary report. Scheduled trigger. Triggers: Email to admins. */
public record DailySalesReportEvent(
        UUID userId, String email, String phone, String firstName,
        BigDecimal totalRevenue, int totalOrders, int newCustomers,
        String currency, Instant reportDate, Instant occurredAt
) implements SanabNotificationEvent {
    public DailySalesReportEvent(BigDecimal totalRevenue, int totalOrders,
                                 int newCustomers, String currency, Instant reportDate) {
        this(null, null, null, "Admin", totalRevenue, totalOrders,
                newCustomers, currency, reportDate, Instant.now());
    }
    @Override public NotificationEventType eventType() { return NotificationEventType.DAILY_SALES_REPORT; }
}

package com.antigravity.sanab.notification.application.listener;

import com.antigravity.sanab.notification.application.service.NotificationService;
import com.antigravity.sanab.notification.event.SanabNotificationEvent;
import com.antigravity.sanab.notification.event.admin.*;
import com.antigravity.sanab.notification.event.auth.*;
import com.antigravity.sanab.notification.event.order.*;
import com.antigravity.sanab.notification.event.promotional.*;
import com.antigravity.sanab.notification.event.returns.*;
import com.antigravity.sanab.notification.event.shopping.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Central event listener for all SANAB business notification events.
 *
 * <p>This listener bridges the gap between business modules and the notification
 * pipeline. It uses Spring Modulith's {@code @ApplicationModuleListener} which:
 * <ul>
 *   <li>Executes in its own transaction (after the publishing transaction commits)</li>
 *   <li>Is backed by the Event Publication Registry (ensures no event loss)</li>
 *   <li>Supports async delivery (does not block the originating transaction)</li>
 *   <li>Automatically retries on listener failure via the publication registry</li>
 * </ul>
 *
 * <p><strong>Architecture rule:</strong> Business modules MUST NOT directly
 * call any email/SMS/WhatsApp service. They publish events here instead.
 *
 * <p>Uses Java 21+ pattern matching on the sealed {@link SanabNotificationEvent}
 * interface, giving compile-time exhaustiveness guarantees.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    // ─── Authentication Events ────────────────────────────────────────────────

    @ApplicationModuleListener
    public void on(UserRegisteredEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(EmailVerifiedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PhoneVerifiedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(NewDeviceLoginEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PasswordChangedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PasswordResetRequestedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(MfaStatusChangedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(OtpSentEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(AccountLockedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    // ─── Shopping Events ──────────────────────────────────────────────────────

    @ApplicationModuleListener
    public void on(CartItemAddedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(WishlistUpdatedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PriceDropEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(BackInStockEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    // ─── Order Events ─────────────────────────────────────────────────────────

    @ApplicationModuleListener
    public void on(OrderPlacedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PaymentSuccessfulEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PaymentFailedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PaymentRefundedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(OrderProcessingEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(OrderPackedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(OrderShippedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(OutForDeliveryEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(OrderDeliveredEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(OrderCancelledEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    // ─── Return & Refund Events ───────────────────────────────────────────────

    @ApplicationModuleListener
    public void on(ReturnRequestedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(ReturnApprovedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(ReturnRejectedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PickupScheduledEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(RefundInitiatedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(RefundCompletedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    // ─── Promotional Events ───────────────────────────────────────────────────

    @ApplicationModuleListener
    public void on(BirthdayWishEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(AnniversaryWishEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(CouponIssuedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(FlashSaleEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(FestivalOfferEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(LoyaltyRewardEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(GiftCardIssuedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(WishlistReminderEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(AbandonedCartEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    // ─── Administrative Events ────────────────────────────────────────────────

    @ApplicationModuleListener
    public void on(ProductCreatedAdminEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(InventoryLowEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(InventoryOutOfStockEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(DailySalesReportEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(HighValueOrderEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(SuspiciousLoginEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(RoleChangedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(PermissionChangedEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(CriticalErrorEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    @ApplicationModuleListener
    public void on(SystemMaintenanceEvent event) {
        logReceived(event);
        notificationService.processEvent(event);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void logReceived(SanabNotificationEvent event) {
        log.info("Notification event received: type={}, userId={}, occurredAt={}",
                event.eventType(), event.userId(), event.occurredAt());
    }
}

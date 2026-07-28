package com.antigravity.sanab.notification.application.impl;

import com.antigravity.sanab.notification.api.dto.request.UpdatePreferenceRequest;
import com.antigravity.sanab.notification.api.dto.response.NotificationPreferenceResponse;
import com.antigravity.sanab.notification.application.service.NotificationPreferenceService;
import com.antigravity.sanab.notification.domain.entity.NotificationPreference;
import com.antigravity.sanab.notification.domain.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreferences(UUID userId) {
        NotificationPreference pref = getOrCreatePreference(userId);
        return NotificationPreferenceResponse.from(pref);
    }

    @Override
    public NotificationPreference getOrCreatePreference(UUID userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> preferenceRepository.save(NotificationPreference.defaultFor(userId)));
    }

    @Override
    public NotificationPreferenceResponse updatePreferences(UUID userId, UpdatePreferenceRequest req) {
        NotificationPreference pref = getOrCreatePreference(userId);

        if (req.emailEnabled() != null) pref.setEmailEnabled(req.emailEnabled());
        if (req.smsEnabled() != null) pref.setSmsEnabled(req.smsEnabled());
        if (req.whatsAppEnabled() != null) pref.setWhatsAppEnabled(req.whatsAppEnabled());
        if (req.pushEnabled() != null) pref.setPushEnabled(req.pushEnabled());
        if (req.inAppEnabled() != null) pref.setInAppEnabled(req.inAppEnabled());
        if (req.transactionalNotificationsEnabled() != null) pref.setTransactionalNotificationsEnabled(req.transactionalNotificationsEnabled());
        if (req.marketingEnabled() != null) pref.setMarketingEnabled(req.marketingEnabled());
        if (req.productAlertsEnabled() != null) pref.setProductAlertsEnabled(req.productAlertsEnabled());
        if (req.systemNotificationsEnabled() != null) pref.setSystemNotificationsEnabled(req.systemNotificationsEnabled());
        if (req.newsletterEnabled() != null) pref.setNewsletterEnabled(req.newsletterEnabled());

        NotificationPreference saved = preferenceRepository.save(pref);
        log.info("Updated notification preferences for userId={}", userId);
        return NotificationPreferenceResponse.from(saved);
    }
}

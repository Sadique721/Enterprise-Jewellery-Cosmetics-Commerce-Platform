package com.antigravity.sanab.promotions.application.service.impl;

import com.antigravity.sanab.promotions.api.dto.request.ApplyCouponRequest;
import com.antigravity.sanab.promotions.api.dto.request.CreateCouponRequest;
import com.antigravity.sanab.promotions.api.dto.response.CouponResponse;
import com.antigravity.sanab.promotions.application.service.PromotionService;
import com.antigravity.sanab.promotions.domain.entity.Coupon;
import com.antigravity.sanab.promotions.domain.enums.DiscountType;
import com.antigravity.sanab.promotions.domain.repository.CouponRepository;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final CouponRepository couponRepository;

    @Override
    public CouponResponse createCoupon(CreateCouponRequest req) {
        String code = req.code().toUpperCase().strip();
        if (couponRepository.existsByCode(code)) {
            throw new SanabException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Coupon code already exists: " + code);
        }

        Coupon coupon = Coupon.builder()
                .code(code)
                .description(req.description())
                .discountType(req.discountType())
                .discountValue(req.discountValue())
                .minimumSpend(req.minimumSpend())
                .maximumDiscountAmount(req.maximumDiscountAmount())
                .usageLimit(req.usageLimit())
                .validFrom(req.validFrom())
                .validUntil(req.validUntil())
                .active(true)
                .build();

        Coupon saved = couponRepository.save(coupon);
        log.info("Created coupon: code={}", code);
        return mapToCouponResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase().strip())
                .orElseThrow(() -> new SanabException(ErrorCode.RESOURCE_NOT_FOUND, "Coupon not found: " + code));
        return mapToCouponResponse(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(ApplyCouponRequest req) {
        Coupon coupon = couponRepository.findByCode(req.code().toUpperCase().strip())
                .orElseThrow(() -> new SanabException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid coupon code"));

        if (!coupon.isValid()) {
            throw new SanabException(ErrorCode.BAD_REQUEST, "Coupon is expired or inactive");
        }

        if (coupon.getMinimumSpend() != null && req.subtotal().compareTo(coupon.getMinimumSpend()) < 0) {
            throw new SanabException(ErrorCode.BAD_REQUEST, "Minimum spend required for coupon: ₹" + coupon.getMinimumSpend());
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = req.subtotal().multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaximumDiscountAmount() != null && discount.compareTo(coupon.getMaximumDiscountAmount()) > 0) {
                discount = coupon.getMaximumDiscountAmount();
            }
        } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discount = coupon.getDiscountValue();
            if (discount.compareTo(req.subtotal()) > 0) {
                discount = req.subtotal();
            }
        }

        return discount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream().map(this::mapToCouponResponse).toList();
    }

    private CouponResponse mapToCouponResponse(Coupon c) {
        return new CouponResponse(
                c.getId(), c.getCode(), c.getDescription(), c.getDiscountType(),
                c.getDiscountValue(), c.getMinimumSpend(), c.getMaximumDiscountAmount(),
                c.getUsageLimit(), c.getUsedCount(), c.getValidFrom(), c.getValidUntil(),
                c.isActive(), c.isValid()
        );
    }
}

package com.antigravity.sanab.promotions.application.service;

import com.antigravity.sanab.promotions.api.dto.request.ApplyCouponRequest;
import com.antigravity.sanab.promotions.api.dto.request.CreateCouponRequest;
import com.antigravity.sanab.promotions.api.dto.response.CouponResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {

    CouponResponse createCoupon(CreateCouponRequest request);

    CouponResponse getCouponByCode(String code);

    BigDecimal calculateDiscount(ApplyCouponRequest request);

    List<CouponResponse> getAllCoupons();
}

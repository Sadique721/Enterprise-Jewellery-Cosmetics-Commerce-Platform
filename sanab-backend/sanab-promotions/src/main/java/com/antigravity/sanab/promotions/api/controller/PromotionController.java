package com.antigravity.sanab.promotions.api.controller;

import com.antigravity.sanab.promotions.api.dto.request.ApplyCouponRequest;
import com.antigravity.sanab.promotions.api.dto.request.CreateCouponRequest;
import com.antigravity.sanab.promotions.api.dto.response.CouponResponse;
import com.antigravity.sanab.promotions.application.service.PromotionService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Coupon management, discount calculation, and marketing promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping("/apply")
    @Operation(summary = "Validate and apply coupon code to cart subtotal")
    public ResponseEntity<ApiResponse<BigDecimal>> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        BigDecimal discountAmount = promotionService.calculateDiscount(request);
        return ResponseEntity.ok(ApiResponse.success(discountAmount, "Coupon applied successfully"));
    }

    @PostMapping("/admin/coupons")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: Create a new promotional coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        CouponResponse response = promotionService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Coupon created successfully"));
    }

    @GetMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Admin: List all promotional coupons")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons() {
        return ResponseEntity.ok(ApiResponse.success(promotionService.getAllCoupons()));
    }
}

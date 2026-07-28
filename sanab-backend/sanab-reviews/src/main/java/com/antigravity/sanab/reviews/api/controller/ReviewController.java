package com.antigravity.sanab.reviews.api.controller;

import com.antigravity.sanab.reviews.api.dto.request.CreateReviewRequest;
import com.antigravity.sanab.reviews.api.dto.response.ReviewResponse;
import com.antigravity.sanab.reviews.application.service.ReviewService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews & Ratings", description = "Product customer reviews, star ratings, and feedback management")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get approved reviews for a product (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getProductReviews(
            @PathVariable UUID productId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getProductReviews(productId, pageable)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a product review")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal String userIdStr,
            @Valid @RequestBody CreateReviewRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        ReviewResponse response = reviewService.createReview(userId, "Customer", request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Review submitted successfully"));
    }

    @GetMapping("/my-reviews")
    @Operation(summary = "Get current user's submitted reviews")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getUserReviews(
            @AuthenticationPrincipal String userIdStr,
            @PageableDefault(size = 10) Pageable pageable) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(reviewService.getUserReviews(userId, pageable)));
    }
}

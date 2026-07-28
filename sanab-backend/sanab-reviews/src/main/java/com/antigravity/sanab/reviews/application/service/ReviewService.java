package com.antigravity.sanab.reviews.application.service;

import com.antigravity.sanab.reviews.api.dto.request.CreateReviewRequest;
import com.antigravity.sanab.reviews.api.dto.response.ReviewResponse;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewService {

    ReviewResponse createReview(UUID userId, String reviewerName, CreateReviewRequest request);

    PagedResponse<ReviewResponse> getProductReviews(UUID productId, Pageable pageable);

    PagedResponse<ReviewResponse> getUserReviews(UUID userId, Pageable pageable);
}

package com.antigravity.sanab.reviews.application.service.impl;

import com.antigravity.sanab.reviews.api.dto.request.CreateReviewRequest;
import com.antigravity.sanab.reviews.api.dto.response.ReviewResponse;
import com.antigravity.sanab.reviews.application.service.ReviewService;
import com.antigravity.sanab.reviews.domain.entity.Review;
import com.antigravity.sanab.reviews.domain.enums.ReviewStatus;
import com.antigravity.sanab.reviews.domain.repository.ReviewRepository;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse createReview(UUID userId, String reviewerName, CreateReviewRequest req) {
        Review review = Review.builder()
                .productId(req.productId())
                .userId(userId)
                .reviewerName(reviewerName != null ? reviewerName : "Verified Buyer")
                .rating(req.rating())
                .title(req.title() != null ? req.title().strip() : null)
                .comment(req.comment().strip())
                .status(ReviewStatus.APPROVED)
                .verifiedPurchase(true)
                .build();

        Review saved = reviewRepository.save(review);
        log.info("Created review for productId={}, userId={}, rating={}", req.productId(), userId, req.rating());
        return mapToReviewResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getProductReviews(UUID productId, Pageable pageable) {
        Page<ReviewResponse> page = reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable)
                .map(this::mapToReviewResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getUserReviews(UUID userId, Pageable pageable) {
        Page<ReviewResponse> page = reviewRepository.findByUserId(userId, pageable)
                .map(this::mapToReviewResponse);
        return PagedResponse.of(page);
    }

    private ReviewResponse mapToReviewResponse(Review r) {
        return new ReviewResponse(
                r.getId(), r.getProductId(), r.getUserId(), r.getReviewerName(),
                r.getRating(), r.getTitle(), r.getComment(), r.getStatus(),
                r.isVerifiedPurchase(), r.getCreatedAt()
        );
    }
}

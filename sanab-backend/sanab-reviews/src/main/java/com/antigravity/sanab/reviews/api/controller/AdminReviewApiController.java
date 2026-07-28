package com.antigravity.sanab.reviews.api.controller;

import com.antigravity.sanab.reviews.domain.repository.ReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enterprise Admin Review Analytics API Controller.
 *
 * <p>Provides rating distribution metrics, monthly review trends, and active reviewer stats.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Review Analytics", description = "Endpoints for review analytics and rating distributions")
public class AdminReviewApiController {

    private final ReviewRepository reviewRepository;

    @GetMapping("/review-stats")
    @Operation(summary = "Get overall review summary statistics")
    public Map<String, Object> getReviewStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalReviews = reviewRepository.count();
        Double avgRating = reviewRepository.getOverallAverageRating();
        long productsWithReviews = reviewRepository.countProductsWithReviews();
        long activeReviewers = reviewRepository.countDistinctCustomers();

        stats.put("totalReviews", totalReviews);
        stats.put("avgRating", avgRating != null ? avgRating : 0.0);
        stats.put("productsWithReviews", productsWithReviews);
        stats.put("activeReviewers", activeReviewers);

        return stats;
    }

    @GetMapping("/rating-distribution")
    @Operation(summary = "Get 1 to 5 star rating distribution")
    public Map<String, Object> getRatingDistribution() {
        Map<String, Object> distribution = new HashMap<>();

        List<Object[]> ratingCounts = reviewRepository.getRatingDistributionAll();

        long oneStar = 0, twoStar = 0, threeStar = 0, fourStar = 0, fiveStar = 0;

        for (Object[] row : ratingCounts) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];

            if (rating != null) {
                switch (rating) {
                    case 1 -> oneStar = count;
                    case 2 -> twoStar = count;
                    case 3 -> threeStar = count;
                    case 4 -> fourStar = count;
                    case 5 -> fiveStar = count;
                }
            }
        }

        distribution.put("oneStar", oneStar);
        distribution.put("twoStar", twoStar);
        distribution.put("threeStar", threeStar);
        distribution.put("fourStar", fourStar);
        distribution.put("fiveStar", fiveStar);

        long totalReviews = reviewRepository.count();
        distribution.put("totalReviews", totalReviews);

        Double avgRating = reviewRepository.getOverallAverageRating();
        distribution.put("avgRating", avgRating != null ? avgRating : 0.0);

        long productsWithReviews = reviewRepository.countProductsWithReviews();
        distribution.put("productsWithReviews", productsWithReviews);

        long activeReviewers = reviewRepository.countDistinctCustomers();
        distribution.put("activeReviewers", activeReviewers);

        return distribution;
    }

    @GetMapping("/monthly-reviews")
    @Operation(summary = "Get monthly review volume for a specific year")
    public Map<String, Object> getMonthlyReviews(@RequestParam(required = false) Integer year) {
        Map<String, Object> response = new HashMap<>();

        if (year == null) {
            year = Year.now().getValue();
        }

        List<Integer> monthlyData = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            monthlyData.add(0);
        }

        List<Object[]> monthlyCounts = reviewRepository.getMonthlyReviewStats(year);

        for (Object[] row : monthlyCounts) {
            if (row[0] != null && row[1] != null) {
                Number monthNum = (Number) row[0];
                Long count = (Long) row[1];
                int month = monthNum.intValue();

                if (month >= 1 && month <= 12) {
                    monthlyData.set(month - 1, count.intValue());
                }
            }
        }

        response.put("year", year);
        response.put("monthlyData", monthlyData);

        return response;
    }
}

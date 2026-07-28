package com.antigravity.sanab.reviews.domain.repository;

import com.antigravity.sanab.reviews.domain.entity.Review;
import com.antigravity.sanab.reviews.domain.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByProductIdAndStatus(UUID productId, ReviewStatus status, Pageable pageable);

    Page<Review> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getOverallAverageRating();

    @Query("SELECT COUNT(DISTINCT r.productId) FROM Review r")
    long countProductsWithReviews();

    @Query("SELECT COUNT(DISTINCT r.userId) FROM Review r")
    long countDistinctCustomers();

    @Query("SELECT r.rating, COUNT(r) FROM Review r GROUP BY r.rating")
    List<Object[]> getRatingDistributionAll();

    @Query("SELECT EXTRACT(MONTH FROM r.createdAt), COUNT(r) FROM Review r WHERE EXTRACT(YEAR FROM r.createdAt) = :year GROUP BY EXTRACT(MONTH FROM r.createdAt)")
    List<Object[]> getMonthlyReviewStats(@Param("year") int year);
}

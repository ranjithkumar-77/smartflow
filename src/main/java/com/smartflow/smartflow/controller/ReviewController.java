package com.smartflow.smartflow.controller;

import com.smartflow.smartflow.dto.ReviewRequest;
import com.smartflow.smartflow.dto.ReviewResponse;
import com.smartflow.smartflow.entity.Review;
import com.smartflow.smartflow.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ReviewResponse createReview(
            @Valid @RequestBody ReviewRequest reviewRequest,
            Authentication authentication) {

        String customerEmail = authentication.getName();

        Review review =
                reviewService.createReview(
                        reviewRequest,
                        customerEmail);

        return convertToResponse(review);
    }

    @GetMapping("/technician/{technicianId}")
    public List<ReviewResponse> getReviewsByTechnician(
            @PathVariable Long technicianId) {

        return reviewService.getReviewsByTechnicianId(technicianId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @GetMapping("/request/{requestId}")
    public ReviewResponse getReviewByRequest(
            @PathVariable Long requestId) {

        Review review = reviewService.getReviewByRequestId(requestId);
        return convertToResponse(review);
    }

    private ReviewResponse convertToResponse(Review review) {

        ReviewResponse response = new ReviewResponse();

        response.setId(review.getId());

        response.setRequestId(
                review.getServiceRequest().getId());

        response.setCustomerId(
                review.getCustomer().getId());

        response.setCustomerName(
                review.getCustomer().getName());

        response.setTechnicianId(
                review.getTechnician().getId());

        response.setTechnicianName(
                review.getTechnician().getUser().getName());

        response.setRating(review.getRating());

        response.setComment(review.getComment());

        response.setCreatedAt(review.getCreatedAt());

        return response;
    }
}
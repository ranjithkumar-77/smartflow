package com.smartflow.smartflow.service;

import com.smartflow.smartflow.dto.ReviewRequest;
import com.smartflow.smartflow.entity.Review;
import com.smartflow.smartflow.entity.ServiceRequest;
import com.smartflow.smartflow.entity.Technician;
import com.smartflow.smartflow.entity.User;
import com.smartflow.smartflow.repository.ReviewRepository;
import com.smartflow.smartflow.repository.ServiceRequestRepository;
import com.smartflow.smartflow.repository.TechnicianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final TechnicianRepository technicianRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ServiceRequestRepository serviceRequestRepository,
            TechnicianRepository technicianRepository) {

        this.reviewRepository = reviewRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.technicianRepository = technicianRepository;
    }

    @Transactional
    public Review createReview(
            ReviewRequest reviewRequest,
            String customerEmail) {

        ServiceRequest serviceRequest =
                serviceRequestRepository.findById(
                        reviewRequest.getRequestId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service request not found"));

        if (!serviceRequest.getStatus().equals("CLOSED")) {
            throw new RuntimeException(
                    "Only closed requests can be reviewed");
        }

        if (serviceRequest.getCustomer() == null) {
            throw new RuntimeException(
                    "Customer not found");
        }

        if (!serviceRequest.getCustomer()
                .getEmail()
                .equals(customerEmail)) {

            throw new RuntimeException(
                    "You are not the customer who created this request");
        }

        if (serviceRequest.getAssignedTechnician() == null) {
            throw new RuntimeException(
                    "No technician assigned to this request");
        }

        if (reviewRepository.existsByServiceRequest(
                serviceRequest)) {

            throw new RuntimeException(
                    "This request has already been reviewed");
        }

        if (reviewRequest.getRating() == null
                || reviewRequest.getRating() < 1
                || reviewRequest.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = new Review();

        review.setServiceRequest(serviceRequest);

        User customer = serviceRequest.getCustomer();
        review.setCustomer(customer);

        Technician technician =
                serviceRequest.getAssignedTechnician();

        review.setTechnician(technician);

        review.setRating(reviewRequest.getRating());

        review.setComment(reviewRequest.getComment());

        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        List<Review> technicianReviews = reviewRepository.findByTechnician(technician);
        double averageRating = technicianReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        technician.setRating(averageRating);
        technicianRepository.save(technician);

        return savedReview;
    }

    public List<Review> getReviewsByTechnicianId(Long technicianId) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        return reviewRepository.findByTechnician(technician).stream().toList();
    }

    public Review getReviewByRequestId(Long requestId) {
        return reviewRepository.findByServiceRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Review not found for this request"));
    }
}
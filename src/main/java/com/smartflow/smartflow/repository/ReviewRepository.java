package com.smartflow.smartflow.repository;

import com.smartflow.smartflow.entity.Review;
import com.smartflow.smartflow.entity.ServiceRequest;
import com.smartflow.smartflow.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByServiceRequest(ServiceRequest serviceRequest);

    Optional<Review> findByServiceRequest(ServiceRequest serviceRequest);

    Optional<Review> findByServiceRequestId(Long serviceRequestId);

    List<Review> findByTechnician(Technician technician);

    List<Review> findByTechnicianId(Long technicianId);
}
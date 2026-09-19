package com.smartflow.smartflow.repository;

import com.smartflow.smartflow.entity.ServiceRequest;
import com.smartflow.smartflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select request from ServiceRequest request where request.id = :requestId")
    Optional<ServiceRequest> findByIdForUpdate(Long requestId);

    List<ServiceRequest> findByCustomer(User customer);

    @Query("select request from ServiceRequest request where request.status = :status")
    List<ServiceRequest> findByStatus(@Param("status") String status);
}
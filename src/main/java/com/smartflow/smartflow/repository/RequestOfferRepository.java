package com.smartflow.smartflow.repository;

import com.smartflow.smartflow.entity.RequestOffer;
import com.smartflow.smartflow.entity.ServiceRequest;
import com.smartflow.smartflow.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface RequestOfferRepository
        extends JpaRepository<RequestOffer, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select offer from RequestOffer offer where offer.id = :offerId")
    Optional<RequestOffer> findByIdForUpdate(Long offerId);

    List<RequestOffer> findByServiceRequest(
            ServiceRequest serviceRequest);

    List<RequestOffer> findByTechnician(
            Technician technician);

    List<RequestOffer> findByTechnicianAndStatus(
            Technician technician,
            String status);

    boolean existsByServiceRequestAndTechnician(
            ServiceRequest serviceRequest,
            Technician technician);

    List<RequestOffer> findByServiceRequestAndStatus(
            ServiceRequest serviceRequest,
            String status);

    void deleteByServiceRequest(ServiceRequest serviceRequest);
}
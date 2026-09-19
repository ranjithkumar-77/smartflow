package com.smartflow.smartflow.service;

import com.smartflow.smartflow.dto.MatchingResult;
import com.smartflow.smartflow.entity.RequestOffer;
import com.smartflow.smartflow.entity.ServiceRequest;
import com.smartflow.smartflow.entity.Technician;
import com.smartflow.smartflow.entity.User;
import com.smartflow.smartflow.repository.RequestOfferRepository;
import com.smartflow.smartflow.repository.ServiceRequestRepository;
import com.smartflow.smartflow.repository.TechnicianRepository;
import com.smartflow.smartflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestOfferService {

    private final RequestOfferRepository requestOfferRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final MatchingService matchingService;
    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;

    public RequestOfferService(
            RequestOfferRepository requestOfferRepository,
            ServiceRequestRepository serviceRequestRepository,
            MatchingService matchingService,
            TechnicianRepository technicianRepository,
            UserRepository userRepository) {

        this.requestOfferRepository = requestOfferRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.matchingService = matchingService;
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
    }

    public List<RequestOffer> getMyOffers(String technicianEmail) {
        User user = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Technician technician = technicianRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Technician profile not found"));

        // Backfill offers for open requests created before this technician logged in.
        for (ServiceRequest request : serviceRequestRepository.findByStatus("OPEN")) {
            List<MatchingResult> matches =
                    matchingService.findMatchingTechnicians(request.getId());

            boolean matchesTechnician = matches.stream()
                    .anyMatch(result -> result.getTechnicianSkill()
                            .getTechnician().getId().equals(technician.getId()));

            if (matchesTechnician
                    && !requestOfferRepository.existsByServiceRequestAndTechnician(request, technician)) {
                RequestOffer offer = new RequestOffer();
                offer.setServiceRequest(request);
                offer.setTechnician(technician);
                offer.setStatus("OFFERED");
                offer.setOfferedAt(LocalDateTime.now());
                requestOfferRepository.save(offer);
            }
        }

        return requestOfferRepository.findByTechnician(technician);
    }

    @Transactional
    public void deleteOpenRequest(Long requestId, String customerEmail) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        if (request.getCustomer() == null
                || !request.getCustomer().getEmail().equals(customerEmail)) {
            throw new RuntimeException("You are not the customer who created this request");
        }

        if (!"OPEN".equals(request.getStatus())) {
            throw new RuntimeException("Only an open request can be deleted");
        }

        requestOfferRepository.deleteAll(
                requestOfferRepository.findByServiceRequest(request));
        requestOfferRepository.flush();
        serviceRequestRepository.delete(request);
    }

    @Transactional
    public List<RequestOffer> createOffers(Long requestId, String customerEmail) {

        ServiceRequest serviceRequest =
                serviceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service request not found"));

        if (serviceRequest.getCustomer() == null) {
            throw new RuntimeException("Customer not found");
        }

        if (!serviceRequest.getCustomer().getEmail().equals(customerEmail)) {
            throw new RuntimeException("You are not the customer who created this request");
        }

        if (!"OPEN".equals(serviceRequest.getStatus())) {
            throw new RuntimeException("Offers can only be created for OPEN requests");
        }

        List<MatchingResult> matchingResults =
                matchingService.findMatchingTechnicians(requestId);

        return matchingResults.stream()
                .filter(result -> {

                    Technician technician =
                            result.getTechnicianSkill()
                                    .getTechnician();

                    return !requestOfferRepository
                            .existsByServiceRequestAndTechnician(
                                    serviceRequest,
                                    technician);
                })
                .map(result -> {

                    Technician technician =
                            result.getTechnicianSkill()
                                    .getTechnician();

                    RequestOffer offer = new RequestOffer();

                    offer.setServiceRequest(serviceRequest);
                    offer.setTechnician(technician);
                    offer.setStatus("OFFERED");
                    offer.setOfferedAt(LocalDateTime.now());

                    return requestOfferRepository.save(offer);
                })
                .toList();
    }

    @Transactional
    public RequestOffer rejectOffer(Long offerId, String technicianEmail) {

        RequestOffer offer =
                requestOfferRepository.findByIdForUpdate(offerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Offer not found"));

        if (offer.getTechnician() == null || offer.getTechnician().getUser() == null) {
            throw new RuntimeException("Offer does not belong to a valid technician");
        }

        if (!offer.getTechnician().getUser().getEmail().equals(technicianEmail)) {
            throw new RuntimeException("You are not the technician who owns this offer");
        }

        if (!offer.getStatus().equals("OFFERED")) {
            throw new RuntimeException(
                    "Offer cannot be rejected");
        }

        offer.setStatus("REJECTED");

        offer.setRespondedAt(
                LocalDateTime.now());

        return requestOfferRepository.save(offer);
    }

    @Transactional
    public RequestOffer acceptOffer(Long offerId, String technicianEmail) {

        RequestOffer offer =
                requestOfferRepository.findByIdForUpdate(offerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Offer not found"));

        if (offer.getTechnician() == null || offer.getTechnician().getUser() == null) {
            throw new RuntimeException("Offer does not belong to a valid technician");
        }

        if (!offer.getTechnician().getUser().getEmail().equals(technicianEmail)) {
            throw new RuntimeException("You are not the technician who owns this offer");
        }

        if (!offer.getStatus().equals("OFFERED")) {
            throw new RuntimeException(
                    "Offer cannot be accepted");
        }

        ServiceRequest request =
                serviceRequestRepository.findByIdForUpdate(
                        offer.getServiceRequest().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Service request not found"));

        if (!request.getStatus().equals("OPEN")) {
            throw new RuntimeException(
                    "Request is already assigned");
        }

        Technician technician =
                offer.getTechnician();

        technician.setCurrentJobs(
                technician.getCurrentJobs() + 1);

        technicianRepository.save(technician);

        request.setAssignedTechnician(technician);
        request.setStatus("ASSIGNED");
        request.setUpdatedAt(LocalDateTime.now());

        offer.setStatus("ACCEPTED");
        offer.setRespondedAt(LocalDateTime.now());

        List<RequestOffer> otherOffers =
                requestOfferRepository
                        .findByServiceRequestAndStatus(
                                request,
                                "OFFERED");

        for (RequestOffer otherOffer : otherOffers) {

            if (!otherOffer.getId().equals(offer.getId())) {

                otherOffer.setStatus("CANCELLED");

                otherOffer.setRespondedAt(
                        LocalDateTime.now());

                requestOfferRepository.save(otherOffer);
            }
        }

        List<RequestOffer> technicianOffers =
                requestOfferRepository.findByTechnicianAndStatus(
                        technician,
                        "OFFERED");

        for (RequestOffer technicianOffer : technicianOffers) {
            if (!technicianOffer.getId().equals(offer.getId())) {
                technicianOffer.setStatus("CANCELLED");
                technicianOffer.setRespondedAt(LocalDateTime.now());
                requestOfferRepository.save(technicianOffer);
            }
        }

        serviceRequestRepository.save(request);

        return requestOfferRepository.save(offer);
    }
}
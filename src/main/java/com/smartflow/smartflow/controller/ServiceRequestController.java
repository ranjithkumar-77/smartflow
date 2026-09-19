package com.smartflow.smartflow.controller;

import com.smartflow.smartflow.dto.ServiceRequestResponse;
import com.smartflow.smartflow.entity.ServiceRequest;
import com.smartflow.smartflow.entity.User;
import com.smartflow.smartflow.repository.UserRepository;
import com.smartflow.smartflow.service.RequestOfferService;
import com.smartflow.smartflow.service.ServiceRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final UserRepository userRepository;
    private final RequestOfferService requestOfferService;

    public ServiceRequestController(
            ServiceRequestService serviceRequestService,
            UserRepository userRepository,
            RequestOfferService requestOfferService) {

        this.serviceRequestService = serviceRequestService;
        this.userRepository = userRepository;
        this.requestOfferService = requestOfferService;
    }

    @PostMapping
    public ServiceRequestResponse createRequest(
            @RequestBody ServiceRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        request.setCustomer(customer);

        ServiceRequest savedRequest =
                serviceRequestService.createRequest(request);

        requestOfferService.createOffers(savedRequest.getId(), email);

        return convertToResponse(savedRequest);
    }

    @GetMapping("/my")
    public List<ServiceRequestResponse> getMyRequests(
            Authentication authentication) {

        String email = authentication.getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ServiceRequest> requests =
                serviceRequestService.getMyRequests(customer);

        return requests.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @DeleteMapping("/{requestId}")
    public void deleteRequest(
            @PathVariable Long requestId,
            Authentication authentication) {

        requestOfferService.deleteOpenRequest(
                requestId,
                authentication.getName());
    }
@PutMapping("/{requestId}/start")
public ServiceRequestResponse startRequest(
        @PathVariable Long requestId,
        Authentication authentication) {

    String technicianEmail = authentication.getName();

    ServiceRequest request =
            serviceRequestService.startRequest(
                    requestId,
                    technicianEmail);

    return convertToResponse(request);
}
     @PutMapping("/{requestId}/resolve")
public ServiceRequestResponse resolveRequest(
        @PathVariable Long requestId,
        Authentication authentication) {

    String technicianEmail = authentication.getName();

    ServiceRequest request =
            serviceRequestService.resolveRequest(
                    requestId,
                    technicianEmail);

    return convertToResponse(request);
}

@PutMapping("/{requestId}/close")
public ServiceRequestResponse closeRequest(
        @PathVariable Long requestId,
        Authentication authentication) {

    String customerEmail = authentication.getName();

    ServiceRequest request =
            serviceRequestService.closeRequest(
                    requestId,
                    customerEmail);

    return convertToResponse(request);
}

    private ServiceRequestResponse convertToResponse(
            ServiceRequest request) {

        ServiceRequestResponse response =
                new ServiceRequestResponse();

        response.setId(request.getId());
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setCategory(request.getCategory());
        response.setLocation(request.getLocation());
        response.setLatitude(request.getLatitude());
        response.setLongitude(request.getLongitude());
        response.setPriority(request.getPriority());
        response.setStatus(request.getStatus());

        response.setCustomerId(request.getCustomer().getId());
        response.setCustomerName(request.getCustomer().getName());
        if (request.getAssignedTechnician() != null) {
            response.setAssignedTechnicianId(request.getAssignedTechnician().getId());
            response.setAssignedTechnicianName(
                    request.getAssignedTechnician().getUser().getName());
        }

        response.setCreatedAt(request.getCreatedAt());
        response.setUpdatedAt(request.getUpdatedAt());

        return response;
    }
}
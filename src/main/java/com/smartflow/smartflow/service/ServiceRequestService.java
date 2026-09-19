package com.smartflow.smartflow.service;

import com.smartflow.smartflow.entity.ServiceRequest;
import com.smartflow.smartflow.entity.Technician;
import com.smartflow.smartflow.entity.User;
import com.smartflow.smartflow.repository.ServiceRequestRepository;
import com.smartflow.smartflow.repository.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final TechnicianRepository technicianRepository;

    public ServiceRequestService(
            ServiceRequestRepository serviceRequestRepository,
            TechnicianRepository technicianRepository) {

        this.serviceRequestRepository = serviceRequestRepository;
        this.technicianRepository = technicianRepository;
    }

    public ServiceRequest createRequest(ServiceRequest request) {

        request.setStatus("OPEN");

        LocalDateTime now = LocalDateTime.now();

        request.setCreatedAt(now);
        request.setUpdatedAt(now);

        return serviceRequestRepository.save(request);
    }

    public List<ServiceRequest> getMyRequests(User customer) {

        return serviceRequestRepository.findByCustomer(customer);
    }

    public ServiceRequest startRequest(
            Long requestId,
            String technicianEmail) {

        ServiceRequest request =
                serviceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service request not found"));

        if (!request.getStatus().equals("ASSIGNED")) {
            throw new RuntimeException(
                    "Request cannot be started");
        }

        if (request.getAssignedTechnician() == null) {
            throw new RuntimeException(
                    "No technician assigned");
        }

        String assignedTechnicianEmail =
                request.getAssignedTechnician()
                        .getUser()
                        .getEmail();

        if (!assignedTechnicianEmail.equals(technicianEmail)) {
            throw new RuntimeException(
                    "You are not the assigned technician");
        }

        request.setStatus("IN_PROGRESS");
        request.setUpdatedAt(LocalDateTime.now());

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest resolveRequest(
            Long requestId,
            String technicianEmail) {

        ServiceRequest request =
                serviceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service request not found"));

        if (!request.getStatus().equals("IN_PROGRESS")) {
            throw new RuntimeException(
                    "Request cannot be resolved");
        }

        if (request.getAssignedTechnician() == null) {
            throw new RuntimeException(
                    "No technician assigned");
        }

        String assignedTechnicianEmail =
                request.getAssignedTechnician()
                        .getUser()
                        .getEmail();

        if (!assignedTechnicianEmail.equals(technicianEmail)) {
            throw new RuntimeException(
                    "You are not the assigned technician");
        }

        request.setStatus("RESOLVED");
        request.setUpdatedAt(LocalDateTime.now());

        return serviceRequestRepository.save(request);
    }

    public ServiceRequest closeRequest(
            Long requestId,
            String customerEmail) {

        ServiceRequest request =
                serviceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service request not found"));

        if (!request.getStatus().equals("RESOLVED")) {
            throw new RuntimeException(
                    "Request cannot be closed");
        }

        if (request.getCustomer() == null) {
            throw new RuntimeException(
                    "No customer found");
        }

        String requestCustomerEmail =
                request.getCustomer().getEmail();

        if (!requestCustomerEmail.equals(customerEmail)) {
            throw new RuntimeException(
                    "You are not the customer who created this request");
        }

        // Decrease technician workload
        if (request.getAssignedTechnician() != null) {

            Integer currentJobs =
                    request.getAssignedTechnician()
                            .getCurrentJobs();

            if (currentJobs != null && currentJobs > 0) {

                Technician technician =
                        request.getAssignedTechnician();

                technician.setCurrentJobs(currentJobs - 1);

                technicianRepository.save(technician);
            }
        }

        request.setStatus("CLOSED");
        request.setUpdatedAt(LocalDateTime.now());

        return serviceRequestRepository.save(request);
    }
}
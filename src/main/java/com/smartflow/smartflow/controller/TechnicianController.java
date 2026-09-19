package com.smartflow.smartflow.controller;

import com.smartflow.smartflow.dto.TechnicianResponse;
import com.smartflow.smartflow.entity.Technician;
import com.smartflow.smartflow.service.TechnicianService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @PostMapping
    public TechnicianResponse createTechnician(
            @RequestBody Technician technician) {

        Technician savedTechnician =
                technicianService.createTechnician(technician);

        return convertToResponse(savedTechnician);
    }

    @GetMapping("/me")
    public TechnicianResponse getMyTechnicianProfile(
            Authentication authentication) {

        return convertToResponse(
                technicianService.getTechnicianByEmail(authentication.getName()));
    }

    @GetMapping("/{id}")
    public TechnicianResponse getTechnician(
            @PathVariable Long id) {

        Technician technician =
                technicianService.getTechnicianById(id);

        return convertToResponse(technician);
    }

    private TechnicianResponse convertToResponse(
            Technician technician) {

        TechnicianResponse response = new TechnicianResponse();

        response.setId(technician.getId());

        response.setUserId(
                technician.getUser().getId()
        );

        response.setName(
                technician.getUser().getName()
        );

        response.setEmail(
                technician.getUser().getEmail()
        );

        response.setPhone(
                technician.getUser().getPhone()
        );

        response.setRole(
                technician.getUser().getRole()
        );

        response.setAvailabilityStatus(
                technician.getAvailabilityStatus()
        );

        response.setLocation(
                technician.getLocation()
        );

        response.setLatitude(
                technician.getLatitude()
        );

        response.setLongitude(
                technician.getLongitude()
        );

        response.setRating(
                technician.getRating()
        );

        response.setCurrentJobs(
                technician.getCurrentJobs()
        );

        return response;
    }
}
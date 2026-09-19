package com.smartflow.smartflow.controller;

import com.smartflow.smartflow.dto.MatchingResult;
import com.smartflow.smartflow.dto.MatchingTechnicianResponse;
import com.smartflow.smartflow.entity.TechnicianSkill;
import com.smartflow.smartflow.service.MatchingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/request/{requestId}")
    public List<MatchingTechnicianResponse> findMatchingTechnicians(
            @PathVariable Long requestId) {

        List<MatchingResult> results =
                matchingService.findMatchingTechnicians(requestId);

        return results.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private MatchingTechnicianResponse convertToResponse(
            MatchingResult result) {

        TechnicianSkill technicianSkill =
                result.getTechnicianSkill();

        MatchingTechnicianResponse response =
                new MatchingTechnicianResponse();

        response.setTechnicianId(
                technicianSkill.getTechnician().getId()
        );

        response.setUserId(
                technicianSkill.getTechnician().getUser().getId()
        );

        response.setName(
                technicianSkill.getTechnician().getUser().getName()
        );

        response.setEmail(
                technicianSkill.getTechnician().getUser().getEmail()
        );

        response.setPhone(
                technicianSkill.getTechnician().getUser().getPhone()
        );

        response.setAvailabilityStatus(
                technicianSkill.getTechnician()
                        .getAvailabilityStatus()
        );

        response.setLatitude(
                technicianSkill.getTechnician().getLatitude()
        );

        response.setLongitude(
                technicianSkill.getTechnician().getLongitude()
        );

        response.setDistanceKm(
                result.getDistanceKm()
        );
        
        response.setMatchingScore(
        result.getMatchingScore()
        ); 
        
        response.setRating(
                technicianSkill.getTechnician().getRating()
        );

        response.setCurrentJobs(
                technicianSkill.getTechnician().getCurrentJobs()
        );

        response.setSkillId(
                technicianSkill.getSkill().getId()
        );

        response.setSkillName(
                technicianSkill.getSkill().getName()
        );

        return response;
    }
}
package com.smartflow.smartflow.controller;

import com.smartflow.smartflow.dto.TechnicianSkillResponse;
import com.smartflow.smartflow.entity.TechnicianSkill;
import com.smartflow.smartflow.service.TechnicianSkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technician-skills")
public class TechnicianSkillController {

    private final TechnicianSkillService technicianSkillService;

    public TechnicianSkillController(
            TechnicianSkillService technicianSkillService) {

        this.technicianSkillService = technicianSkillService;
    }

    @PostMapping("/{technicianId}/{skillId}")
    public TechnicianSkill assignSkill(
            @PathVariable Long technicianId,
            @PathVariable Long skillId) {

        return technicianSkillService.assignSkill(
                technicianId,
                skillId
        );
    }

    @GetMapping("/skill/{skillId}")
    public List<TechnicianSkillResponse> getTechniciansBySkill(
            @PathVariable Long skillId) {

        List<TechnicianSkill> technicianSkills =
                technicianSkillService.getTechniciansBySkill(skillId);

        return technicianSkills.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private TechnicianSkillResponse convertToResponse(
            TechnicianSkill technicianSkill) {

        TechnicianSkillResponse response =
                new TechnicianSkillResponse();

        response.setTechnicianSkillId(
                technicianSkill.getId()
        );

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
                technicianSkill.getTechnician().getAvailabilityStatus()
        );

        response.setLatitude(
                technicianSkill.getTechnician().getLatitude()
        );

        response.setLongitude(
                technicianSkill.getTechnician().getLongitude()
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

        response.setSkillDescription(
                technicianSkill.getSkill().getDescription()
        );

        return response;
    }
}
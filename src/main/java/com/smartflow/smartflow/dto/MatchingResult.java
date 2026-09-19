package com.smartflow.smartflow.dto;

import com.smartflow.smartflow.entity.TechnicianSkill;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchingResult {

    private TechnicianSkill technicianSkill;

    private Double distanceKm;
    private Double matchingScore;

    public MatchingResult(
            TechnicianSkill technicianSkill,
            Double distanceKm) {

        this.technicianSkill = technicianSkill;
        this.distanceKm = distanceKm;
    }
}
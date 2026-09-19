package com.smartflow.smartflow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchingTechnicianResponse {

    private Long technicianId;
    private Long userId;

    private String name;
    private String email;
    private String phone;

    private String availabilityStatus;

    private Double latitude;
    private Double longitude;
    private Double distanceKm;
    private Double matchingScore;

    private Double rating;
    private Integer currentJobs;

    private Long skillId;
    private String skillName;
}
package com.smartflow.smartflow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianSkillResponse {

    private Long technicianSkillId;

    private Long technicianId;
    private Long userId;

    private String name;
    private String email;
    private String phone;

    private String availabilityStatus;

    private Double latitude;
    private Double longitude;

    private Double rating;
    private Integer currentJobs;

    private Long skillId;
    private String skillName;
    private String skillDescription;
}
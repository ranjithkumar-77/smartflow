package com.smartflow.smartflow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianResponse {

    private Long id;

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;

    private String availabilityStatus;
    private String location;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private Integer currentJobs;
}
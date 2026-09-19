package com.smartflow.smartflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ServiceRequestResponse {

    private Long id;

    private String title;

    private String description;

    private String category;

    private String location;

    private Double latitude;

    private Double longitude;

    private String priority;

    private String status;

    private Long customerId;

    private String customerName;
    private Long assignedTechnicianId;
    private String assignedTechnicianName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
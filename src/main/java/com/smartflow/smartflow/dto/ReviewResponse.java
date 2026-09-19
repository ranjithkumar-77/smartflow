package com.smartflow.smartflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {

    private Long id;

    private Long requestId;

    private Long customerId;

    private String customerName;

    private Long technicianId;

    private String technicianName;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}
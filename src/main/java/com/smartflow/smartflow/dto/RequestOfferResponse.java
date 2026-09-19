package com.smartflow.smartflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestOfferResponse {

    private Long id;

    private Long requestId;
    private String requestTitle;
    private String requestCategory;
    private String requestDescription;
    private String requestPriority;
    private String customerName;
    private String requestStatus;
    private String requestLocation;
    private Double requestLatitude;
    private Double requestLongitude;

    private Long technicianId;
    private Long technicianUserId;
    private String technicianName;

    private String status;

    private LocalDateTime offeredAt;
    private LocalDateTime respondedAt;
}
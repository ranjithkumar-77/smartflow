package com.smartflow.smartflow.controller;

import com.smartflow.smartflow.dto.RequestOfferResponse;
import com.smartflow.smartflow.entity.RequestOffer;
import com.smartflow.smartflow.service.RequestOfferService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-offers")
public class RequestOfferController {

    private final RequestOfferService requestOfferService;

    public RequestOfferController(
            RequestOfferService requestOfferService) {

        this.requestOfferService = requestOfferService;
    }

    @GetMapping
    public List<RequestOfferResponse> getMyOffers(
            Authentication authentication) {

        String technicianEmail = authentication.getName();

        List<RequestOffer> offers =
                requestOfferService.getMyOffers(technicianEmail);

        return offers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @GetMapping("/my")
    public List<RequestOfferResponse> getMyOffersForDocumentedRoute(
            Authentication authentication) {

        return getMyOffers(authentication);
    }

    @PostMapping("/{requestId}")
    public List<RequestOfferResponse> createOffers(
            @PathVariable Long requestId,
            Authentication authentication) {

        String customerEmail = authentication.getName();

        List<RequestOffer> offers =
                requestOfferService.createOffers(requestId, customerEmail);

        return offers.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @PutMapping("/{offerId}/reject")
    public RequestOfferResponse rejectOffer(
            @PathVariable Long offerId,
            Authentication authentication) {

        String technicianEmail = authentication.getName();

        RequestOffer offer =
                requestOfferService.rejectOffer(offerId, technicianEmail);

        return convertToResponse(offer);
    }

    @PutMapping("/{offerId}/accept")
    public RequestOfferResponse acceptOffer(
            @PathVariable Long offerId,
            Authentication authentication) {

        String technicianEmail = authentication.getName();

        RequestOffer offer = requestOfferService.acceptOffer(offerId, technicianEmail);

        return convertToResponse(offer);
    }

    private RequestOfferResponse convertToResponse(
            RequestOffer offer) {

        RequestOfferResponse response =
                new RequestOfferResponse();

        response.setId(offer.getId());

        response.setRequestId(
                offer.getServiceRequest().getId()
        );

        response.setRequestTitle(
                offer.getServiceRequest().getTitle()
        );
        response.setRequestCategory(offer.getServiceRequest().getCategory());
        response.setRequestDescription(offer.getServiceRequest().getDescription());
        response.setRequestPriority(offer.getServiceRequest().getPriority());
        response.setRequestStatus(offer.getServiceRequest().getStatus());
        response.setCustomerName(offer.getServiceRequest().getCustomer().getName());

        response.setRequestLocation(
                offer.getServiceRequest().getLocation()
        );

        response.setRequestLatitude(
                offer.getServiceRequest().getLatitude()
        );

        response.setRequestLongitude(
                offer.getServiceRequest().getLongitude()
        );

        response.setTechnicianId(
                offer.getTechnician().getId()
        );

        response.setTechnicianUserId(
                offer.getTechnician().getUser().getId()
        );

        response.setTechnicianName(
                offer.getTechnician().getUser().getName()
        );

        response.setStatus(
                offer.getStatus()
        );

        response.setOfferedAt(
                offer.getOfferedAt()
        );

        response.setRespondedAt(
                offer.getRespondedAt()
        );

        return response;
    }
}
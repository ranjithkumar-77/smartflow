package com.smartflow.smartflow.service;

import com.smartflow.smartflow.dto.MatchingResult;
import com.smartflow.smartflow.entity.ServiceRequest;
import com.smartflow.smartflow.entity.TechnicianSkill;
import com.smartflow.smartflow.repository.ServiceRequestRepository;
import com.smartflow.smartflow.repository.TechnicianSkillRepository;
import com.smartflow.smartflow.util.DistanceUtil;
import org.springframework.stereotype.Service;
import com.smartflow.smartflow.util.MatchingScoreUtil;

import java.util.List;

@Service
public class MatchingService {

    private static final double MAX_MATCH_DISTANCE_KM = 25.0;

    private final ServiceRequestRepository serviceRequestRepository;
    private final TechnicianSkillRepository technicianSkillRepository;

    public MatchingService(
            ServiceRequestRepository serviceRequestRepository,
            TechnicianSkillRepository technicianSkillRepository) {

        this.serviceRequestRepository = serviceRequestRepository;
        this.technicianSkillRepository = technicianSkillRepository;
    }

    public List<MatchingResult> findMatchingTechnicians(
            Long requestId) {

        // 1. Find the service request
        ServiceRequest request = serviceRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException("Service request not found"));

        // 2. Get the category of the request
        String category = request.getCategory();

        // 3. Find technicians with the required skill
        //    and ONLINE_AVAILABLE status.
        //    If no specific technician type is chosen, route to all available technicians.
        List<TechnicianSkill> technicians;

        if (category == null || category.isBlank() || "ANY".equalsIgnoreCase(category)) {
            technicians = technicianSkillRepository
                    .findByTechnicianAvailabilityStatus("ONLINE_AVAILABLE");
        } else {
            String requestedCategory = category.trim();
            technicians = technicianSkillRepository
                    .findByTechnicianAvailabilityStatus("ONLINE_AVAILABLE")
                    .stream()
                    .filter(technicianSkill -> technicianSkill.getSkill().getName()
                            .replaceAll("[^A-Za-z0-9]", "")
                            .toLowerCase()
                            .contains(requestedCategory
                                    .replaceAll("[^A-Za-z0-9]", "")
                                    .toLowerCase()))
                    .toList();
        }

        // 4. Calculate distance and create matching results
      return technicians.stream()
        .map(technicianSkill -> {

            if (request.getLatitude() == null || request.getLongitude() == null
                    || technicianSkill.getTechnician().getLatitude() == null
                    || technicianSkill.getTechnician().getLongitude() == null) {
                return null;
            }

            double distance = DistanceUtil.calculateDistance(
                    request.getLatitude(),
                    request.getLongitude(),
                    technicianSkill.getTechnician().getLatitude(),
                    technicianSkill.getTechnician().getLongitude()
            );

          if (distance > MAX_MATCH_DISTANCE_KM) {
              return null;
          }

          double rating = technicianSkill.getTechnician().getRating() == null
                  ? 0.0
                  : technicianSkill.getTechnician().getRating();

          int currentJobs = technicianSkill.getTechnician().getCurrentJobs() == null
                  ? 0
                  : technicianSkill.getTechnician().getCurrentJobs();

          double matchingScore =
                  MatchingScoreUtil.calculateScore(
                          distance,
                          rating,
                          currentJobs
                  );

          MatchingResult result =
                  new MatchingResult(
                          technicianSkill,
                          distance
                  );

          result.setMatchingScore(matchingScore);

          return result;
      })
      .filter(result -> result != null)
      .sorted((result1, result2) ->
          Double.compare(
                  result2.getMatchingScore(),
                  result1.getMatchingScore()
          )
      )
      .toList();
}
}
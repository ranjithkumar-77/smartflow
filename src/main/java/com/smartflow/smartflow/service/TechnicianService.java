package com.smartflow.smartflow.service;

import com.smartflow.smartflow.entity.Technician;
import com.smartflow.smartflow.entity.User;
import com.smartflow.smartflow.repository.TechnicianRepository;
import com.smartflow.smartflow.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;

    public TechnicianService(
            TechnicianRepository technicianRepository,
            UserRepository userRepository) {

        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
    }

    public Technician createTechnician(Technician technician) {

        Long userId = technician.getUser().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        technician.setUser(user);

        if (technician.getAvailabilityStatus() == null) {
            technician.setAvailabilityStatus("ONLINE_AVAILABLE");
        }

        if (technician.getLocation() == null || technician.getLocation().isBlank()) {
            throw new RuntimeException("Technician location is required");
        }

        if (technician.getLatitude() == null || technician.getLongitude() == null) {
            throw new RuntimeException("Technician latitude and longitude are required");
        }

        if (technician.getRating() == null) {
            technician.setRating(0.0);
        }

        if (technician.getCurrentJobs() == null) {
            technician.setCurrentJobs(0);
        }

        return technicianRepository.save(technician);
    }

    public Technician getTechnicianById(Long id) {

        return technicianRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Technician not found"));
    }

    public Technician getTechnicianByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return technicianRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Technician profile not found"));
    }
}
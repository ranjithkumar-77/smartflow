package com.smartflow.smartflow.repository;

import com.smartflow.smartflow.entity.TechnicianSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicianSkillRepository
        extends JpaRepository<TechnicianSkill, Long> {

    List<TechnicianSkill> findBySkillId(Long skillId);

    List<TechnicianSkill> findByTechnicianAvailabilityStatus(String availabilityStatus);

    List<TechnicianSkill>
    findBySkillNameAndTechnicianAvailabilityStatus(
            String skillName,
            String availabilityStatus
    );
}
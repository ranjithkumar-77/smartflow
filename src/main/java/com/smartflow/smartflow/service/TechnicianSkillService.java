package com.smartflow.smartflow.service;

import com.smartflow.smartflow.entity.Skill;
import com.smartflow.smartflow.entity.Technician;
import com.smartflow.smartflow.entity.TechnicianSkill;
import com.smartflow.smartflow.repository.SkillRepository;
import com.smartflow.smartflow.repository.TechnicianRepository;
import com.smartflow.smartflow.repository.TechnicianSkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianSkillService {

    private final TechnicianSkillRepository technicianSkillRepository;
    private final TechnicianRepository technicianRepository;
    private final SkillRepository skillRepository;

    public TechnicianSkillService(
            TechnicianSkillRepository technicianSkillRepository,
            TechnicianRepository technicianRepository,
            SkillRepository skillRepository) {

        this.technicianSkillRepository = technicianSkillRepository;
        this.technicianRepository = technicianRepository;
        this.skillRepository = skillRepository;
    }

    public TechnicianSkill assignSkill(
            Long technicianId,
            Long skillId) {

        Technician technician = technicianRepository
                .findById(technicianId)
                .orElseThrow(() ->
                        new RuntimeException("Technician not found"));

        Skill skill = skillRepository
                .findById(skillId)
                .orElseThrow(() ->
                        new RuntimeException("Skill not found"));

        TechnicianSkill technicianSkill = new TechnicianSkill();

        technicianSkill.setTechnician(technician);
        technicianSkill.setSkill(skill);

        return technicianSkillRepository.save(technicianSkill);
    }

    public List<TechnicianSkill> getTechniciansBySkill(Long skillId) {

        return technicianSkillRepository.findBySkillId(skillId);
    }
}
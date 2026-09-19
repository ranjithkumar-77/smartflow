package com.smartflow.smartflow.config;

import com.smartflow.smartflow.entity.Skill;
import com.smartflow.smartflow.repository.SkillRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SkillSeeder implements ApplicationRunner {

    private final SkillRepository skillRepository;

    public SkillSeeder(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> defaultSkills = List.of(
                "Plumber",
                "Electrician",
                "AC Technician",
                "TV Technician",
                "Carpenter",
                "Painter",
                "Appliance Repair",
                "General Technician"
        );

        for (String name : defaultSkills) {
            skillRepository.findByName(name).orElseGet(() -> {
                Skill skill = new Skill();
                skill.setName(name);
                skill.setDescription("Service category for " + name);
                return skillRepository.save(skill);
            });
        }
    }
}

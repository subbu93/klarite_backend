package com.klarite.backend.controller;

import com.klarite.backend.dto.Skill;
import com.klarite.backend.dto.SkillAssignment;
import com.klarite.backend.dto.SkillEpisodes;
import com.klarite.backend.service.AdminSkillService;
import com.klarite.backend.service.SkillAssignedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class SkillAssignedController {
    @Autowired
    private SkillAssignedService skillAssignedService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/assign_skill/get_assigned_skills")
    public List<SkillAssignment> getAllAssignedSkills(@RequestParam(value = "id") long id) {
        return skillAssignedService.getAllAssignedSkills(id, jdbcTemplate);
    }
}

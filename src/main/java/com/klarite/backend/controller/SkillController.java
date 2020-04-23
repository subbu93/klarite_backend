package com.klarite.backend.controller;

import com.klarite.backend.dto.*;
import com.klarite.backend.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class SkillController {
    @Autowired
    private SkillService skillService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/assign_skill/get_all_assignments")
    public List<SkillAssignment> getAllAssignedSkills() {
        return skillService.getAllAssignedSkills(jdbcTemplate);
    }

    @DeleteMapping("/assign_skill/delete_assigned_skill")
    public ResponseEntity<Object> deleteAssignment(@RequestParam(value = "id") Long id) {
        return skillService.deleteAssignment(id, jdbcTemplate);
    }

    @GetMapping("/assign_skill/get_cost_centers")
    public List<CostCenter> getCostCenters() {
        return skillService.getCostCenters(jdbcTemplate);
    }

    @GetMapping("/assign_skill/get_business_units")
    public List<BusinessUnit> getBusinessUnits() {
        return skillService.getBusinessUnits(jdbcTemplate);
    }

    @PostMapping("/assign_skill/add_skill_assignment")
    public ResponseEntity<Object> addSkillAssignment(@RequestBody SkillAssignment skillAssignment) {
        return skillService.addSkillAssignment(skillAssignment, jdbcTemplate);
    }

    @GetMapping("/skill/get_all_episodes")
    public List<Episode> getAllEpisodes(@RequestParam(value = "userId") long userId,
                                        @RequestParam(value = "skillId") long skillId) {
        return skillService.getAllEpisodes(userId, skillId, jdbcTemplate);
    }

    @GetMapping("/skill/get_episode")
    public Episode getEpisode(@RequestParam(value = "id") long id) {
        return skillService.getEpisode(id, jdbcTemplate);
    }

    @GetMapping("/skill/get_assigned_skills")
    public List<Skill> getAssignedSkills(@RequestParam(required = false, value = "userId") Long userId) {
        return skillService.getAssignedSkills(userId, jdbcTemplate);
    }
}
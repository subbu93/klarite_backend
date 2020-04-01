package com.klarite.backend.controller;

import com.klarite.backend.dto.BusinessUnit;
import com.klarite.backend.dto.CostCenter;
import com.klarite.backend.dto.SkillAssignment;
import com.klarite.backend.service.SkillAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class SkillAssignmentController {
    @Autowired
    private SkillAssignmentService skillAssignmentService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/assign_skill/get_assigned_skills")
    public List<SkillAssignment> getAllAssignedSkills(@RequestParam(required = false, value = "id") Long id) {
        return skillAssignmentService.getAllAssignedSkills(id, jdbcTemplate);
    }

    @DeleteMapping("/assign_skill/delete_assigned_skill")
    public ResponseEntity<Object> deleteAssignment(@RequestParam(value = "id") Long id) {
        return skillAssignmentService.deleteAssignment(id, jdbcTemplate);
    }

    @GetMapping("/assign_skill/get_cost_centers")
    public List<CostCenter> getCostCenters() {
        return skillAssignmentService.getCostCenters(jdbcTemplate);
    }

    @GetMapping("/assign_skill/get_business_units")
    public List<BusinessUnit> getBusinessUnits() {
        return skillAssignmentService.getBusinessUnits(jdbcTemplate);
    }

    @PostMapping("/assign_skill/add_skill_assignment")
    public ResponseEntity<Object> addSkillAssignment(@RequestBody SkillAssignment skillAssignment) {
        return skillAssignmentService.addSkillAssignment(skillAssignment, jdbcTemplate);
    }
}

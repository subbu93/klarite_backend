package com.klarite.backend.service;

import com.klarite.backend.dto.BusinessUnit;
import com.klarite.backend.dto.CostCenter;
import com.klarite.backend.dto.SkillAssignment;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SkillAssignmentService {
    List<SkillAssignment> getAllAssignedSkills(Long id, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> deleteAssignment(Long id, JdbcTemplate jdbcTemplate);

    List<CostCenter> getCostCenters(JdbcTemplate jdbcTemplate);

    List<BusinessUnit> getBusinessUnits(JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> addSkillAssignment(SkillAssignment skillAssignment, JdbcTemplate jdbcTemplate);
}

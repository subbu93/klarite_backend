package com.klarite.backend.service;

import com.klarite.backend.dto.SkillAssignment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SkillAssignmentService {
    List<SkillAssignment> getAllAssignedSkills(Long id, JdbcTemplate jdbcTemplate);

    List<SkillAssignment> getAssignedSkills(Long assignmentId, JdbcTemplate jdbcTemplate);
}

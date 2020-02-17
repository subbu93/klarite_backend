package com.klarite.backend.service;

import com.klarite.backend.dto.SkillAssignment;
import com.klarite.backend.dto.SkillEpisodes;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SkillAssignedService {
    List<SkillAssignment> getAllAssignedSkills(long id, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> addSkillEpisodes(SkillEpisodes skillEpisodes, JdbcTemplate jdbcTemplate);
}

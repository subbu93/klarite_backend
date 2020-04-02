package com.klarite.backend.service;

import java.util.List;

import com.klarite.backend.dto.BusinessUnit;
import com.klarite.backend.dto.CostCenter;
import com.klarite.backend.dto.Episode;
import com.klarite.backend.dto.SkillAssignment;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public interface SkillService {
    List<Episode> getAllEpisodes(Long userId, Long skillId, JdbcTemplate jdbcTemplate);
    Episode getEpisode(Long episodeId, JdbcTemplate jdbcTemplate);

    List<SkillAssignment> getAllAssignedSkills(Long id, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> deleteAssignment(Long id, JdbcTemplate jdbcTemplate);

    List<CostCenter> getCostCenters(JdbcTemplate jdbcTemplate);

    List<BusinessUnit> getBusinessUnits(JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> addSkillAssignment(SkillAssignment skillAssignment, JdbcTemplate jdbcTemplate);
}

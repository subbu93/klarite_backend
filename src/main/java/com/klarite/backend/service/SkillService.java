package com.klarite.backend.service;

import com.klarite.backend.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public interface SkillService {
    List<Episode> getAllEpisodes(Long userId, Long skillId, JdbcTemplate jdbcTemplate);

    Episode getEpisode(Long episodeId, JdbcTemplate jdbcTemplate);

    List<SkillAssignment> getAllAssignedSkills(JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> deleteAssignment(Long id, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> setObserverId(Long episodeId, Long observerId, JdbcTemplate jdbcTemplate);

    List<CostCenter> getCostCenters(JdbcTemplate jdbcTemplate);

    List<BusinessUnit> getBusinessUnits(JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> addSkillAssignment(SkillAssignment skillAssignment, JdbcTemplate jdbcTemplate);

    List<Skill> getAssignedSkills(Long userId, JdbcTemplate jdbcTemplate);

    Map<Long, List<Skill>> getAnalysisData(Long businessUnitId, Long costCenterId, JdbcTemplate jdbcTemplate);

    List<GraphData> getUsersPerSkillData(Long businessUnitId, Long costCenterId, Long skillId, JdbcTemplate jdbcTemplate);

    List<ValidationData> getSkillValidation(Long userId, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> saveSkillValidation(List<ValidationData> validationDataList, JdbcTemplate jdbcTemplate);
}

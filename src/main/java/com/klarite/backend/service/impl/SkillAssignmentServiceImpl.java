package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.BusinessUnit;
import com.klarite.backend.dto.CostCenter;
import com.klarite.backend.dto.SkillAssignment;
import com.klarite.backend.service.SkillAssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SkillAssignmentServiceImpl implements SkillAssignmentService {

    @Override
    public List<SkillAssignment> getAllAssignedSkills(Long userId, JdbcTemplate jdbcTemplate) {
        String sAssignmentQuery = "SELECT * FROM " + Constants.TABLE_S_ASSIGNMENTS;
        String skillAssignmentQuery = "SELECT * FROM " + Constants.TABLE_SKILL_ASSIGNMENTS +
                " WHERE assignment_id = ?";
        String costCenterIdQuery = "SELECT cost_center.* " +
                "FROM   " + Constants.TABLE_USERS +
                "       INNER JOIN " + Constants.TABLE_COST_CENTER +
                "               ON users.cost_center_id = cost_center.id " +
                "WHERE  users.id = ? ";
        String thresholdQuery = "SELECT * FROM " + Constants.TABLE_SKILLS + " WHERE id = ?";

        List<SkillAssignment> skillAssignments = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sAssignmentQuery);

        for (Map<String, Object> row : rows) {
            SkillAssignment obj = new SkillAssignment();
            List<Map<String, Object>> userRows = jdbcTemplate.queryForList(skillAssignmentQuery, (Long) row.get("id"));
            if (userRows.size() == 0) {
                continue;
            }
            obj.setAssignmentId((Long) row.get("id"));
            List<Long> temp = new ArrayList<>();
            for (Map<String, Object> userRow : userRows) {
                temp.add((Long) userRow.get("user_id"));
            }
            obj.setAssignedUserIds(temp);
            obj.setSkillId((Long) row.get("skill_id"));
            obj.setSkillAssignmentName((String) row.get("name"));

            Map<String, Object> costCenterIdRow = jdbcTemplate.queryForMap(costCenterIdQuery, obj.getAssignedUserIds().get(0));
            obj.setCostCenterName((String) costCenterIdRow.get("name"));
            obj.setCostCenterId((Integer) costCenterIdRow.get("id"));
            obj.setCompletionDate((Date) row.get("completion_date"));
            obj.setSkillValidatorId((Long) row.get("validator_id"));
            if (userId != null) {
                obj.setEpisodeCount(getEpisodeCount((Long) row.get("skill_id"), userId, jdbcTemplate));
            }

            Map<String, Object> thresholdRow = jdbcTemplate.queryForMap(thresholdQuery, obj.getSkillId());
            obj.setSkillThreshold((Integer) thresholdRow.get("threshold"));

            skillAssignments.add(obj);
        }
        return skillAssignments;
    }

    @Override
    public ResponseEntity<Object> deleteAssignment(Long id, JdbcTemplate jdbcTemplate) {
        try {
            deleteSkillAssignment(id, jdbcTemplate);
            deleteSAssignment(id, jdbcTemplate);
            return new ResponseEntity<>("Deleted Assignment", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<CostCenter> getCostCenters(JdbcTemplate jdbcTemplate) {
        String query = "SELECT * from " + Constants.TABLE_COST_CENTER;
        List<CostCenter> costCenters;
        try {
            costCenters = new ArrayList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
            for (Map<String, Object> row : rows) {
                CostCenter costCenter = new CostCenter();
                costCenter.setId((Integer) row.get("id"));
                costCenter.setCostCenterName((String) row.get("name"));
                costCenters.add(costCenter);
            }
            return costCenters;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<BusinessUnit> getBusinessUnits(JdbcTemplate jdbcTemplate) {
        String query = "SELECT * from " + Constants.TABLE_BUSINESS_UNIT;
        List<BusinessUnit> businessUnits;
        try {
            businessUnits = new ArrayList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
            for (Map<String, Object> row : rows) {
                BusinessUnit businessUnit = new BusinessUnit();
                businessUnit.setId((Integer) row.get("id"));
                businessUnit.setBusinessUnitName((String) row.get("name"));
                businessUnits.add(businessUnit);
            }
            return businessUnits;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public ResponseEntity<Object> addSkillAssignment(SkillAssignment skillAssignment, JdbcTemplate jdbcTemplate) {
        if (skillAssignment.getAssignmentId() != null) {
            return updateSkillAssignment(skillAssignment, jdbcTemplate);
        } else {
            String insertSAssignmentQuery = "INSERT INTO " + Constants.TABLE_S_ASSIGNMENTS +
                    " VALUES(?,?,?,?); SELECT SCOPE_IDENTITY() as id;";
            try {
                Map<String, Object> row = jdbcTemplate.queryForMap(insertSAssignmentQuery, skillAssignment.getSkillId(),
                        skillAssignment.getSkillAssignmentName(), skillAssignment.getCompletionDate(),
                        skillAssignment.getSkillValidatorId());

                BigDecimal assignmentId = (BigDecimal) row.get("id");

                insertSkillAssignment(assignmentId.longValue(), skillAssignment, jdbcTemplate);
                return new ResponseEntity<>("Updated", HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    private ResponseEntity<Object> updateSkillAssignment(SkillAssignment skillAssignment, JdbcTemplate jdbcTemplate) {
        String updateSAssignmentQuery = "UPDATE " + Constants.TABLE_S_ASSIGNMENTS +
                " SET skill_id = ?, name= ?, completion_date = ?, validator_id = ? " +
                " WHERE id = ?;";
        try {
            jdbcTemplate.update(updateSAssignmentQuery, skillAssignment.getSkillId(),
                    skillAssignment.getSkillAssignmentName(), skillAssignment.getCompletionDate(),
                    skillAssignment.getSkillValidatorId(), skillAssignment.getAssignmentId());

            deleteSkillAssignment(skillAssignment.getAssignmentId(), jdbcTemplate);
            insertSkillAssignment(skillAssignment.getAssignmentId(), skillAssignment, jdbcTemplate);

            ResponseEntity<Object> response = new ResponseEntity<>("Stored", HttpStatus.CREATED);
            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void insertSkillAssignment(Long assignmentId, SkillAssignment skillAssignment, JdbcTemplate jdbcTemplate) {
        String insertSkillAssignmentQuery = "INSERT INTO " + Constants.TABLE_SKILL_ASSIGNMENTS +
                " VALUES(?,?);";

        for (Long userId : skillAssignment.getAssignedUserIds()) {
            jdbcTemplate.update(insertSkillAssignmentQuery, assignmentId, userId);
        }
    }

    private void deleteSAssignment(Long assignmentId, JdbcTemplate jdbcTemplate) {
        String deleteSAssignmentQuery = "DELETE FROM " + Constants.TABLE_S_ASSIGNMENTS +
                " WHERE  id = ? ";
        jdbcTemplate.update(deleteSAssignmentQuery, assignmentId);
    }

    private void deleteSkillAssignment(Long assignmentId, JdbcTemplate jdbcTemplate) {
        String deleteSkillAssignmentQuery = "DELETE FROM " + Constants.TABLE_SKILL_ASSIGNMENTS +
                " WHERE  assignment_id = ? ";
        jdbcTemplate.update(deleteSkillAssignmentQuery, assignmentId);
    }

    private Integer getEpisodeCount(Long skillId, Long userId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT Count(*) AS count " +
                "FROM   skill_episodes " +
                "WHERE  skill_id = ? " +
                "       AND episode_id IN (SELECT id " +
                "                          FROM   episodes " +
                "                          WHERE  user_id = ?); ";
        Map<String, Object> row = jdbcTemplate.queryForMap(query, skillId, userId);
        return (Integer) row.get("count");
    }
}

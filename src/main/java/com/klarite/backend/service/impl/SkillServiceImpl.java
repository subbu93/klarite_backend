package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.*;
import com.klarite.backend.service.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkillServiceImpl implements SkillService {

    @Override
    public List<SkillAssignment> getAllAssignedSkills(JdbcTemplate jdbcTemplate) {
        String sAssignmentQuery = "SELECT * FROM " + Constants.TABLE_S_ASSIGNMENTS;
        String skillAssignmentQuery = "SELECT * FROM " + Constants.TABLE_SKILL_ASSIGNMENTS +
                " WHERE assignment_id = ?";
        String costCenterIdQuery = "SELECT cost_center.* " +
                "FROM   " + Constants.TABLE_USERS + " AS u " +
                "       INNER JOIN " + Constants.TABLE_COST_CENTER +
                "               ON users.cost_center_id = cost_center.id " +
                "WHERE  u.id = ?" +
                "       AND u.soft_delete = 0;";

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
            obj.setCompletionDate((java.util.Date) row.get("completion_date"));
            obj.setSkillValidatorId((Long) row.get("validator_id"));

//            Map<String, Object> thresholdRow = jdbcTemplate.queryForMap(thresholdQuery, obj.getSkillId());
//            obj.setSkillThreshold((Integer) thresholdRow.get("threshold"));

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

    @Override
    public List<Skill> getAssignedSkills(Long userId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * " +
                " FROM " + Constants.TABLE_SKILLS +
                " WHERE  id IN (SELECT skill_id " +
                "              FROM " + Constants.TABLE_S_ASSIGNMENTS +
                "              WHERE  id IN (SELECT assignment_id " +
                "                            FROM " + Constants.TABLE_SKILL_ASSIGNMENTS +
                "                            WHERE  user_id = ?)) ";
        List<Skill> skills;
        try {
            skills = new ArrayList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, userId);
            for (Map<String, Object> row : rows) {
                Skill skill = new Skill();

                skill.setId(((Long) row.get("id")));
                skill.setSkillName((String) row.get("name"));
                skill.setDescription((String) row.get("description"));
                skill.setThreshold((Integer) row.get("threshold"));
                skill.setSkillTrainingPreRequisite((String) row.get("training_prerequisite"));
                skill.setTrainingId((Long) row.get("training_id"));
                skill.setEpisodeCount(getEpisodeCount(skill.getId(), userId, jdbcTemplate));
                skills.add(skill);
            }
            return skills;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Map<Long, List<Skill>> getAnalysisData(Long businessUnitId,
                                                  Long costCenterId,
                                                  JdbcTemplate jdbcTemplate) {
        String getUserFromCostCenterQuery = "SELECT id " +
                " FROM "+ Constants.TABLE_USERS +
                " WHERE  cost_center_id = ? " +
                "       AND business_unit_id = ?" +
                "       AND soft_delete = 0;";
        Map<Long, List<Skill>> assignedSkillsForAllUsers;
        try {
            assignedSkillsForAllUsers = new HashMap<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(getUserFromCostCenterQuery,
                    costCenterId, businessUnitId);
            for (Map<String, Object> row : rows) {
                List<Skill> assignedSkillForEachUser = getAssignedSkills((Long) row.get("id"), jdbcTemplate);
                assignedSkillsForAllUsers.put((Long) row.get("id"), assignedSkillForEachUser);
            }
        } catch (Exception e) {
            return new HashMap<>();
        }
        return assignedSkillsForAllUsers;
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

    @Override
    public List<Episode> getAllEpisodes(Long userId, Long skillId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * FROM episodes WHERE user_id = $user";
        query = query.replace("$user", userId.toString());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        return getEpisodes(rows, skillId, jdbcTemplate);
    }

    @Override
    public Episode getEpisode(Long episodeId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * FROM episodes WHERE id = $id";
        query = query.replace("$id", episodeId.toString());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        List<Episode> res = getEpisodes(rows, null, jdbcTemplate);
        if (res.size() == 1)
            return res.get(0);
        else
            return null;
    }

    private List<Episode> getEpisodes(List<Map<String, Object>> querryResult, Long skillId, JdbcTemplate jdbcTemplate) {
        List<Episode> skillEpisodes = new ArrayList<>();
        String query;
        for (Map<String, Object> row : querryResult) {

            Long episode_id = (Long) row.get("id");
            if (skillId != null) {
                query = "SELECT * FROM skill_episodes WHERE episode_id = $e_id  AND skill_id = $skill";
                query = query.replace("$e_id", episode_id.toString()).replace("$skill", skillId.toString());
            } else {
                query = "SELECT * FROM skill_episodes WHERE episode_id = $e_id";
                query = query.replace("$e_id", episode_id.toString());
            }

            List<Map<String, Object>> new_rows = jdbcTemplate.queryForList(query);

            if (new_rows.size() > 0) {
                Episode obj = new Episode();
                obj.setId(episode_id);
                obj.setUserId((Long) row.get("user_id"));
                obj.setDate((Date) row.get("date"));
                obj.setMrn((String) row.get("mrn"));
                obj.setAudited((boolean) row.get("is_audited"));
                for (Map<String, Object> new_row : new_rows) {
                    SkillEpisode skillEpisode = new SkillEpisode();
                    skillEpisode.setSkillId((Long) new_row.get("skill_id"));
                    skillEpisode.setObserved((boolean) new_row.get("is_observed"));
                    skillEpisode.setObserverId((Long) new_row.get("observer_id"));
                    obj.getEpisodes().add(skillEpisode);
                }
                skillEpisodes.add(obj);
            }
        }
        return skillEpisodes;
    }
}

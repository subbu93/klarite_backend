package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.*;
import com.klarite.backend.dto.Notification.SkillValidationNotification;
import com.klarite.backend.service.AdminService;
import com.klarite.backend.service.NotificationService;
import com.klarite.backend.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkillServiceImpl implements SkillService {
    @Autowired
    private AdminService adminService;
    @Autowired
    private NotificationService notificationService;

    @Override
    public List<SkillAssignment> getAllAssignedSkills(JdbcTemplate jdbcTemplate) {
        String sAssignmentQuery = "SELECT * FROM " + Constants.TABLE_S_ASSIGNMENTS;
        String skillAssignmentQuery = "SELECT * FROM " + Constants.TABLE_SKILL_ASSIGNMENTS +
                " WHERE assignment_id = ?";
        String costCenterIdQuery = "SELECT cost_center.* " +
                "FROM   " + Constants.TABLE_USERS + " AS u " +
                "       INNER JOIN " + Constants.TABLE_COST_CENTER +
                "               ON u.cost_center_id = cost_center.id " +
                "WHERE  u.id = ?" +
                "       AND u.soft_delete = 0;";
        String businessUnitIdQuery = "SELECT business_unit.* " +
                "FROM   " + Constants.TABLE_USERS + " AS u " +
                "       INNER JOIN " + Constants.TABLE_BUSINESS_UNIT +
                "               ON u.cost_center_id = business_unit.id " +
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

            Map<String, Object> costCenterIdRow = jdbcTemplate.queryForMap(costCenterIdQuery,
                    obj.getAssignedUserIds().get(0));
            obj.setCostCenterName((String) costCenterIdRow.get("name"));
            obj.setCostCenterId((Integer) costCenterIdRow.get("id"));
            Map<String, Object> businessUnitIdRow = jdbcTemplate.queryForMap(businessUnitIdQuery,
                    obj.getAssignedUserIds().get(0));
            obj.setBusinessUnitName((String) businessUnitIdRow.get("name"));
            obj.setBusinessUnitId((Integer) businessUnitIdRow.get("id"));
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
                skill.setTotalThreshold((Integer) row.get("total_threshold"));
                skill.setValidationThreshold((Integer) row.get("validation_threshold"));
                skill.setSkillTrainingPreRequisite((String) row.get("training_prerequisite"));
                skill.setTrainingId((Long) row.get("training_id"));
                skill.setEpisodeCount(getEpisodeCount(skill.getId(), userId, jdbcTemplate));
                skill.setValidatedEpisodeCount(getValidatedEpisodeCount(skill.getId(), userId, jdbcTemplate));
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
                " FROM " + Constants.TABLE_USERS +
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

    @Override
    public List<GraphData> getUsersPerSkillData(Long businessUnitId, Long costCenterId,
                                                Long skillId, JdbcTemplate jdbcTemplate) {
        String getUserFromCostCenterQuery = "SELECT Distinct * " +
                "                   FROM " + Constants.TABLE_SKILL_ASSIGNMENTS +
                "                   WHERE  assignment_id IN (SELECT id " +
                "                                            FROM " + Constants.TABLE_S_ASSIGNMENTS +
                "                                            WHERE  skill_id = ?);";

        List<GraphData> usersPerSkillData;
        try {
            usersPerSkillData = new ArrayList<>();
            UserServiceImpl userService = new UserServiceImpl();
            AdminServiceImpl adminService = new AdminServiceImpl();
            Skill skill = adminService.getSkill(skillId, jdbcTemplate);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(getUserFromCostCenterQuery,
                    skillId);
            for (Map<String, Object> row : rows) {
                GraphData temp = new GraphData();
                User user = userService.getUser((Long) row.get("user_id"), jdbcTemplate);
                temp.setName(user.getFirstName() + " " + user.getLastName());
                temp.setValue(getEpisodeCount(skillId, user.getId(), jdbcTemplate));
                Map<String, Long> map = new HashMap<>();
                map.put("threshold", (long) skill.getTotalThreshold());
                map.put("userId", user.getId());
                temp.setExtra(map);

                usersPerSkillData.add(temp);
            }
            return usersPerSkillData;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<ValidationData> getSkillValidation(Long userId, JdbcTemplate jdbcTemplate) {
        String getValidationListQuery = "SELECT t2.*, " +
                "       users.first_name, " +
                "       users.last_name " +
                "FROM   (SELECT t1.*, " +
                "               skills.name, " +
                "               skills.total_threshold, " +
                "               skills.validation_threshold " +
                "        FROM   (SELECT * " +
                "                FROM " + Constants.TABLE_SKILL_EPISODES + " se " +
                "                       INNER JOIN " + Constants.TABLE_EPISODES +
                "                               ON se.episode_id = episodes.id " +
                "                WHERE  se.observer_id = ? AND se.is_validated = 0 AND se.is_remediated = 0) AS t1 " +
                "               INNER JOIN " + Constants.TABLE_SKILLS +
                "                       ON t1.skill_id = skills.id " +
                "        WHERE  skills.soft_delete = 0) AS t2 " +
                "       INNER JOIN " + Constants.TABLE_USERS +
                "               ON t2.user_id = users.id " +
                "WHERE  users.soft_delete = 0;";

        try {
            List<ValidationData> validationData = new ArrayList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(getValidationListQuery, userId);

            for (Map<String, Object> row : rows) {
                ValidationData temp = new ValidationData();
                temp.setEpisodeId((long) row.get("episode_id"));
                temp.setSkillId((long) row.get("skill_id"));
                temp.setSkillName((String) row.get("name"));
                temp.setUserId((long) row.get("user_id"));
                temp.setObserverId((long) row.get("observer_id"));
                temp.setMrn((String) row.get("mrn"));
                temp.setFirstName((String) row.get("first_name"));
                temp.setLastName((String) row.get("last_name"));
                temp.setDate((row.get("date")).toString());
                temp.setValidated((boolean) row.get("is_validated"));
                temp.setObserved((boolean) row.get("is_observed"));
                temp.setRemediated((boolean) row.get("is_remediated"));

                validationData.add(temp);
            }
            return validationData;
        } catch (SQLWarningException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public ResponseEntity<Object> setObserverId(Long episodeId, Long observerId, JdbcTemplate jdbcTemplate) {
        String query = "UPDATE " + Constants.TABLE_SKILL_EPISODES + "SET observer_id = ? WHERE episode_id = ? AND is_observed = 1";
        jdbcTemplate.update(query, observerId, episodeId);
        return new ResponseEntity<>("Stored", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> saveSkillValidation(List<ValidationData> validationDataList, JdbcTemplate jdbcTemplate) {
        String updateSkillEpisodesQuery = "UPDATE " + Constants.TABLE_SKILL_EPISODES +
                " SET is_validated = ?, is_remediated = ?, comment = ? " +
                " WHERE episode_id = ? AND skill_id = ?";
        try {
            for (ValidationData validationData : validationDataList) {
                if (validationData.isValidated() || validationData.isRemediated()) {
                    jdbcTemplate.update(updateSkillEpisodesQuery, validationData.isValidated(),
                            validationData.isRemediated(), validationData.getComment(),
                            validationData.getEpisodeId(), validationData.getSkillId());

                    // Store Notification
                    UserServiceImpl userService = new UserServiceImpl();
                    User usr = userService.getUser(validationData.getObserverId(), jdbcTemplate);
                    SkillValidationNotification orn = new SkillValidationNotification();
                    if (validationData.isValidated()) {
                        orn.setValidated(true);
                    } else {
                        orn.setValidated(false);
                    }
                    orn.setComment(validationData.getComment());
                    orn.setEpisodeId(validationData.getEpisodeId());
                    orn.setSkillId(validationData.getSkillId());
                    orn.setSkillName(validationData.getSkillName());
                    notificationService.add(orn, usr, validationData.getUserId(), jdbcTemplate);
                }
            }
            return new ResponseEntity<>("Stored", HttpStatus.CREATED);
        } catch (SQLWarningException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
                "FROM  " + Constants.TABLE_SKILL_EPISODES +
                " WHERE  skill_id = ? " +
                "       AND episode_id IN (SELECT id " +
                "                          FROM   episodes " +
                "                          WHERE  user_id = ?); ";
        Map<String, Object> row = jdbcTemplate.queryForMap(query, skillId, userId);
        return (Integer) row.get("count");
    }

    private Integer getValidatedEpisodeCount(Long skillId, Long userId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT Count(*) AS count " +
                "FROM  " + Constants.TABLE_SKILL_EPISODES +
                " WHERE  skill_id = ? " +
                "       AND is_validated = 1" +
                "       AND episode_id IN (SELECT id " +
                "                          FROM   episodes " +
                "                          WHERE  user_id = ?); ";
        Map<String, Object> row = jdbcTemplate.queryForMap(query, skillId, userId);
        return (Integer) row.get("count");
    }

    @Override
    public List<Episode> getAllEpisodes(Long userId, Long skillId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * FROM " + Constants.TABLE_EPISODES + " WHERE user_id = $user";
        query = query.replace("$user", userId.toString());

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        return getEpisodes(rows, skillId, jdbcTemplate);
    }

    @Override
    public Episode getEpisode(Long episodeId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * FROM " + Constants.TABLE_EPISODES + " WHERE id = $id";
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
                query = "SELECT * FROM " + Constants.TABLE_SKILL_EPISODES + " WHERE episode_id = $e_id  AND skill_id = $skill";
                query = query.replace("$e_id", episode_id.toString()).replace("$skill", skillId.toString());
            } else {
                query = "SELECT * FROM " + Constants.TABLE_SKILL_EPISODES + " WHERE episode_id = $e_id";
                query = query.replace("$e_id", episode_id.toString());
            }

            List<Map<String, Object>> new_rows = jdbcTemplate.queryForList(query);

            if (new_rows.size() > 0) {
                Episode obj = new Episode();
                obj.setId(episode_id);
                obj.setUserId((Long) row.get("user_id"));
                obj.setDate((row.get("date")).toString());
                obj.setMrn((String) row.get("mrn"));
                for (Map<String, Object> new_row : new_rows) {
                    SkillEpisode skillEpisode = new SkillEpisode();
                    skillEpisode.setSkillId((Long) new_row.get("skill_id"));
                    skillEpisode.setObserved((boolean) new_row.get("is_observed"));
                    skillEpisode.setObserverId((Long) new_row.get("observer_id"));
                    skillEpisode.setComment((String) new_row.get("comment"));
                    skillEpisode.setValidated((Boolean) new_row.get("is_validated"));
                    skillEpisode.setRemediated((Boolean) new_row.get("is_remediated"));
                    skillEpisode.setSkillName(adminService.getSkill(skillEpisode.getSkillId(), jdbcTemplate).getSkillName());
                    obj.getEpisodes().add(skillEpisode);
                }
                skillEpisodes.add(obj);
            }
        }
        return skillEpisodes;
    }
}

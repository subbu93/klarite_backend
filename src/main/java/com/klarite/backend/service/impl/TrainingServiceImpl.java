package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.TrainingAssignment;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.TrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TrainingServiceImpl implements TrainingService {
    @Override
    public List<TrainingAssignment> getAllAssignedTrainings(Long userId, JdbcTemplate jdbcTemplate) {
        String tAssignmentQuery = "SELECT * FROM " + Constants.TABLE_T_ASSIGNMENTS;
        String trainingAssignmentQuery = "SELECT * FROM " + Constants.TABLE_TRAINING_ASSIGNMENTS +
                " WHERE assignment_id = ?";
        String costCenterIdQuery = "SELECT cost_center.* " +
                "FROM   " + Constants.TABLE_USERS +
                "       INNER JOIN " + Constants.TABLE_COST_CENTER +
                "               ON users.cost_center_id = cost_center.id " +
                "WHERE  users.id = ? ";
        String businessUnitIdQuery = "SELECT business_unit.* " +
                "FROM   " + Constants.TABLE_USERS +
                "       INNER JOIN " + Constants.TABLE_BUSINESS_UNIT +
                "               ON users.business_unit_id = business_unit.id " +
                "WHERE  users.id = ? ";

        List<TrainingAssignment> trainingAssignments = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(tAssignmentQuery);
        for (Map<String, Object> row : rows) {
            TrainingAssignment obj = new TrainingAssignment();
            List<Map<String, Object>> userRows = jdbcTemplate.queryForList(trainingAssignmentQuery, (Long) row.get("id"));
            if (userRows.size() == 0) {
                continue;
            }
            obj.setAssignmentId((Long) row.get("id"));
            List<Long> temp = new ArrayList<>();
            for (Map<String, Object> userRow : userRows) {
                temp.add((Long) userRow.get("user_id"));
            }
            obj.setAssignedUserIds(temp);
            obj.setTrainingId((Long) row.get("training_id"));
            obj.setTrainingAssignmentName((String) row.get("name"));

            Map<String, Object> costCenterIdRow = jdbcTemplate.queryForMap(costCenterIdQuery, obj.getAssignedUserIds().get(0));
            Map<String, Object> businessUnitIdRow = jdbcTemplate.queryForMap(businessUnitIdQuery, obj.getAssignedUserIds().get(0));
            obj.setCostCenter((String) costCenterIdRow.get("name"));
            obj.setCostCenterId((Integer) costCenterIdRow.get("id"));
            obj.setBusinessUnit((String) businessUnitIdRow.get("name"));
            obj.setBusinessUnitId((Integer) businessUnitIdRow.get("id"));
            obj.setDate((Date) row.get("date"));
            obj.setStartTime((Time) row.get("start_time"));
            obj.setLocation((String) row.get("location"));
            obj.setUuid((String) row.get("uuid"));

            trainingAssignments.add(obj);
        }
        return trainingAssignments;
    }

    @Override
    public ResponseEntity<Object> deleteAssignment(Long id, JdbcTemplate jdbcTemplate) {
        try {
            deleteTrainingAssignment(id, jdbcTemplate);
            deleteTAssignment(id, jdbcTemplate);
            return new ResponseEntity<>("Deleted Assignment", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> addTrainingAssignment(TrainingAssignment trainingAssignment, JdbcTemplate jdbcTemplate) {
        if (trainingAssignment.getAssignmentId() != null) {
            return updateTrainingAssignment(trainingAssignment, jdbcTemplate);
        } else {
            String insertTAssignmentQuery = "INSERT INTO " + Constants.TABLE_T_ASSIGNMENTS +
                    " VALUES(?,?,?,?,?,?); SELECT SCOPE_IDENTITY() as id;";
            try {
                Map<String, Object> row = jdbcTemplate.queryForMap(insertTAssignmentQuery,
                        trainingAssignment.getTrainingAssignmentName(),
                        trainingAssignment.getTrainingId(),
                        trainingAssignment.getDate(),
                        trainingAssignment.getStartTime(),
                        trainingAssignment.getLocation(),
                        trainingAssignment.getUuid());

                BigDecimal assignmentId = (BigDecimal) row.get("id");

                insertTrainingAssignment(assignmentId.longValue(), trainingAssignment, jdbcTemplate);
                return new ResponseEntity<>("Updated", HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Override
    public List<User> getAttendanceList(JdbcTemplate jdbcTemplate) {
        return null;
    }

    private ResponseEntity<Object> updateTrainingAssignment(TrainingAssignment trainingAssignment,
                                                            JdbcTemplate jdbcTemplate) {
        String updateTAssignmentQuery = "UPDATE " + Constants.TABLE_T_ASSIGNMENTS +
                " SET name = ?, training_id= ?, date = ?, start_time = ?, location = ? " +
                " WHERE id = ?;";
        try {
            jdbcTemplate.update(updateTAssignmentQuery, trainingAssignment.getTrainingAssignmentName(),
                    trainingAssignment.getTrainingId(), trainingAssignment.getDate(),
                    trainingAssignment.getStartTime(), trainingAssignment.getLocation(),
                    trainingAssignment.getAssignmentId());

            deleteTrainingAssignment(trainingAssignment.getAssignmentId(), jdbcTemplate);
            insertTrainingAssignment(trainingAssignment.getAssignmentId(), trainingAssignment, jdbcTemplate);

            ResponseEntity<Object> response = new ResponseEntity<>("Stored", HttpStatus.CREATED);
            return response;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void insertTrainingAssignment(long assignmentId, TrainingAssignment trainingAssignment,
                                          JdbcTemplate jdbcTemplate) {
        String insertTrainingAssignmentQuery = "INSERT INTO " + Constants.TABLE_TRAINING_ASSIGNMENTS +
                " VALUES(?,?);";

        for (Long userId : trainingAssignment.getAssignedUserIds()) {
            jdbcTemplate.update(insertTrainingAssignmentQuery, assignmentId, userId);
        }
    }

    private void deleteTrainingAssignment(Long assignmentId, JdbcTemplate jdbcTemplate) {
        String deleteSkillAssignmentQuery = "DELETE FROM " + Constants.TABLE_TRAINING_ASSIGNMENTS +
                " WHERE  assignment_id = ? ";
        jdbcTemplate.update(deleteSkillAssignmentQuery, assignmentId);
    }

    private void deleteTAssignment(Long assignmentId, JdbcTemplate jdbcTemplate) {
        String deleteSAssignmentQuery = "DELETE FROM " + Constants.TABLE_T_ASSIGNMENTS +
                " WHERE  id = ? ";
        jdbcTemplate.update(deleteSAssignmentQuery, assignmentId);
    }
}

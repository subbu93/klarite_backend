package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.TrainingAssignment;
import com.klarite.backend.dto.User;
import com.klarite.backend.dto.Notification.UpcomingTrainingNotification;
import com.klarite.backend.service.NotificationService;
import com.klarite.backend.service.TrainingService;
import com.klarite.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    @Autowired
    private NotificationService notificationService;
    @Autowired 
    private UserService userService;

    @Override
    public List<TrainingAssignment> getAllAssignedTrainings(Long userId, JdbcTemplate jdbcTemplate) {
        String tAssignmentQuery = "SELECT * FROM " + Constants.TABLE_T_ASSIGNMENTS;
        String trainingAssignmentQuery = "SELECT * FROM " + Constants.TABLE_TRAINING_ASSIGNMENTS +
                " WHERE assignment_id = ?";
        String costCenterIdQuery = "SELECT cost_center.* " +
                "FROM   " + Constants.TABLE_USERS +
                "       INNER JOIN " + Constants.TABLE_COST_CENTER +
                "               ON users.cost_center_id = cost_center.id " +
                "WHERE  users.id = ? " +
                "       AND soft_delete = 0;";
        String businessUnitIdQuery = "SELECT business_unit.* " +
                "FROM   " + Constants.TABLE_USERS +
                "       INNER JOIN " + Constants.TABLE_BUSINESS_UNIT +
                "               ON users.business_unit_id = business_unit.id " +
                "WHERE  users.id = ? " +
                "       AND soft_delete = 0;";

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
                Long tempUserId = (Long) userRow.get("user_id");
                temp.add(tempUserId);
                if (userId != null && tempUserId == userId) {
                    obj.setAttended((Boolean) userRow.get("attended"));
                }
            }
            if (userId != null && !temp.contains(userId))
                continue;

            obj.setAssignedUserIds(temp);
            obj.setTrainingId((Long) row.get("training_id"));
            obj.setTrainingAssignmentName((String) row.get("name"));
            obj.setTrainingName(getTrainingName(obj.getAssignmentId(), jdbcTemplate));

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
    public TrainingAssignment getAssignmentDetails(Long assignmentId, JdbcTemplate jdbcTemplate) {
        try {
        String query = "SELECT training_id, date, start_time, location from " + Constants.TABLE_T_ASSIGNMENTS + "where id = ?";
        Map<String, Object> row = jdbcTemplate.queryForMap(query, assignmentId);
        TrainingAssignment assignment = new TrainingAssignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setLocation((String) row.get("location"));
        assignment.setDate((Date) row.get("date"));
        assignment.setStartTime((Time) row.get("start_time"));
        query = "SELECT name FROM" + Constants.TABLE_TRAININGS + "WHERE id = ?";
        row = jdbcTemplate.queryForMap(query, (Long) row.get("training_id"));
        assignment.setTrainingName((String) row.get("name"));
        return assignment;
        } catch (DataAccessException e) {
            return null;
        }
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
    public List<User> getAttendanceList(Long trainingAssignmentId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * " +
                "FROM   " + Constants.TABLE_USERS + " AS u " +
                "       INNER JOIN (SELECT * " +
                "                   FROM " + Constants.TABLE_TRAINING_ASSIGNMENTS +
                "                   WHERE  assignment_id = ?) AS t1 " +
                "               ON u.id = t1.user_id " +
                " WHERE  soft_delete = 0; ";

        List<User> users;
        try {
            users = new ArrayList<>();
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, trainingAssignmentId);

            for (Map<String, Object> row : rows) {
                User user = new User();

                user.setId(((Long) row.get("id")));
                user.setOsuId((String) row.get("osu_id"));
                user.setFirstName((String) row.get("first_name"));
                user.setMiddleName((String) row.get("middle_name"));
                user.setLastName((String) row.get("last_name"));
                user.setEmail((String) row.get("email"));
                user.setBusinessUnitId((Integer) row.get("business_unit_id"));
                user.setCostCenterId((Integer) row.get("cost_center_id"));
                user.setBusinessUnitName((String) row.get("business_unit"));
                user.setCostCenterName((String) row.get("cost_center"));
                user.setUrl((String) row.get("image_url"));
                user.setFirstLogin((Boolean) row.get("first_time_user"));
                user.setTrainingAttended((boolean) row.get("attended"));

                users.add(user);
            }
            return users;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<User>();
        }
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
                " VALUES(?,?,0);";

        for (Long userId : trainingAssignment.getAssignedUserIds()) {
            jdbcTemplate.update(insertTrainingAssignmentQuery, assignmentId, userId);
            UpcomingTrainingNotification notification = new UpcomingTrainingNotification();
            notification.setTrainingAssignmentId(assignmentId);
            User usr = userService.getUser(userId, false, jdbcTemplate);    //todo: this should be replaced with admin id
            notificationService.add(notification, usr, userId, jdbcTemplate);
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

    private String getTrainingName(Long assignmentId, JdbcTemplate jdbcTemplate) {
        String getTrainingNameQuery = "SELECT name FROM " + Constants.TABLE_TRAININGS +
                " WHERE  id in ( SELECT training_id FROM" + Constants.TABLE_T_ASSIGNMENTS +
                " WHERE id = ? )";

        Map<String, Object> row = jdbcTemplate.queryForMap(getTrainingNameQuery, assignmentId);
        return (String) row.get("name");
    }
}

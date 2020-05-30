package com.klarite.backend.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.klarite.backend.Constants;
import com.klarite.backend.Privileges;
import com.klarite.backend.dto.Notification.*;
import com.klarite.backend.dto.ReadNotification;
import com.klarite.backend.dto.TrainingAssignment;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.AdminService;
import com.klarite.backend.service.NotificationService;
import com.klarite.backend.service.SkillService;
import com.klarite.backend.service.TrainingService;
import com.klarite.backend.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private TrainingService trainingService;

    @Override
    public ResponseEntity<Object> delete(Long id, JdbcTemplate jdbcTemplate) {
        try {
            String deleteQuery = "DELETE FROM " + Constants.TABLE_NOTIFICATIONS + " WHERE id = ?";
            jdbcTemplate.update(deleteQuery, id);
            return new ResponseEntity<>(Constants.MSG_DELETE_SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Constants.MSG_DELETE_FAILED, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> respond(Long id, Long userId, Boolean accepted, String comment,
            JdbcTemplate jdbcTemplate) {
        try {
            ObservationRequestNotification notification = getRequestNotification(id, jdbcTemplate);
            User recevr = userService.getUser(notification.getSenderId(), false, jdbcTemplate);

            User sender = userService.getUser(userId, false, jdbcTemplate);
            ObservationResponseNotification orn = new ObservationResponseNotification();
            orn.setEpisodeId(notification.getEpisodeId());
            orn.setComment(comment);
            orn.setIsAccepted(accepted);
            add(orn, sender, recevr.getId(), jdbcTemplate);
            if (accepted) {
                markNotificationInactive(id, jdbcTemplate);
                skillService.setObserverId(notification.getEpisodeId(), userId, jdbcTemplate);
            }

            return new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<Notification> get(Long userId, Boolean getActive, JdbcTemplate jdbcTemplate) {
        List<Notification> notifications = new ArrayList<>();
        try {
            User usr = userService.getUser(userId, false, jdbcTemplate);
            String query = "SELECT TOP (15) * FROM " + Constants.TABLE_NOTIFICATIONS + " WHERE receiver_id = %1";
            query = query.replace("%1", userId.toString());
            if (Privileges.skillObserver.contains(usr.getRole())) {
                query += " OR (business_unit_id = %1 AND cost_center_id = %2 AND type = %3) ";
                query = query.replace("%1", usr.getBusinessUnitId().toString()).replace("%2",
                        usr.getCostCenterId().toString());
                query = query.replace("%3", Integer.toString(NotificationType.ObservationRequest));
            }
            if (getActive) {
                query += " AND is_active = %1";
                query = query.replace("%1", "1");
            }
            query += " ORDER by id DESC ";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
            for (Map<String, Object> row : rows) {
                int type = (Integer) row.get("type");
                switch (type) {
                    case NotificationType.ObservationRequest:
                        notifications.add(getRequestedObservations(row, usr, jdbcTemplate));
                        break;
                    case NotificationType.ObservationResponse:
                        notifications.add(getObservationResponse(row, usr, jdbcTemplate));
                        break;
                    case NotificationType.UpcomingTraining:
                        notifications.add(getUpcomingTrainings(row, usr, jdbcTemplate));
                        break;
                    case NotificationType.SkillValidation:
                        notifications.add(getValidationNotification(row, usr, jdbcTemplate));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifications;
    }


    @Override
    public ResponseEntity<Object> markRead(ReadNotification ids, JdbcTemplate jdbcTemplate) {
        try {
            for (Long notificationId : ids.getIds()) {
                markNotificationInactive(notificationId, jdbcTemplate);
            }
            return new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private Notification getValidationNotification(Map<String, Object> row, User usr, JdbcTemplate jdbcTemplate) {
        Long senderId = (Long) row.get("sender_id");
        User sender = userService.getUser(senderId, jdbcTemplate);

        SkillValidationNotification obj = new SkillValidationNotification();
        obj.init((Long) row.get("id"), (Boolean) row.get("is_active"), usr.getId(), usr.fetchFullName(), senderId,
                sender.fetchFullName(), (String) row.get("payload"));
        obj.setSkillName(adminService.getSkill(obj.getSkillId(), jdbcTemplate).getSkillName());
        return obj;
    }

    private Notification getRequestedObservations(Map<String, Object> row, User usr, JdbcTemplate jdbcTemplate) {
        Long senderId = (Long) row.get("sender_id");
        User sender = userService.getUser(senderId, jdbcTemplate);

        ObservationRequestNotification obj = new ObservationRequestNotification();
        obj.init((Long) row.get("id"), (Boolean) row.get("is_active"), usr.getId(), usr.fetchFullName(), senderId,
                sender.fetchFullName(), (String) row.get("payload"));

        List<String> skillNames = new ArrayList<>();
        for (Long skillId : obj.getSkills()) {
            skillNames.add(adminService.getSkill(skillId, jdbcTemplate).getSkillName());
        }
        obj.setSkillNames(StringUtils.join(skillNames, ", "));
        return obj;
    }

    private Notification getUpcomingTrainings(Map<String, Object> row, User usr, JdbcTemplate jdbcTemplate) {
        UpcomingTrainingNotification obj = new UpcomingTrainingNotification();
        obj.init((Long) row.get("id"), (Boolean) row.get("is_active"), usr.getId(), usr.fetchFullName(), null, null,
                (String) row.get("payload"));

        TrainingAssignment assignment = trainingService.getAssignmentDetails(obj.getTrainingAssignmentId(),
                jdbcTemplate);
        java.util.Date date = new java.util.Date();
        String dateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(date);

        if (dateStr == assignment.getDate().toString()) {
            obj.setLocation(assignment.getLocation());
            obj.setTraningName(assignment.getTrainingName());
            obj.setTimeStamp(assignment.getDate().toString() + " " + assignment.getStartTime().toString());
            return obj;
        }
        return null;
    }

    private Notification getObservationResponse(Map<String, Object> row, User usr, JdbcTemplate jdbcTemplate) {
        Long senderId = (Long) row.get("sender_id");
        User sender = userService.getUser(senderId, jdbcTemplate);

        ObservationResponseNotification obj = new ObservationResponseNotification();
        obj.init((Long) row.get("id"), (Boolean) row.get("is_active"), (Long) row.get("receiver_id"),
                usr.fetchFullName(), senderId, sender.fetchFullName(), (String) row.get("payload"));
        return obj;
    }

    private ObservationRequestNotification getRequestNotification(Long id, JdbcTemplate jdbcTemplate) {

        String query = "SELECT * FROM " + Constants.TABLE_NOTIFICATIONS + " WHERE id = ?";
        Map<String, Object> row = jdbcTemplate.queryForMap(query, id);

        Long senderId = (Long) row.get("sender_id");
        Long recevrId = (Long) row.get("receiver_id");
        User sender = userService.getUser(senderId, jdbcTemplate);
        User recevr = userService.getUser(recevrId, jdbcTemplate);
        String recevrName = recevr == null ? "" : recevr.fetchFullName();

        ObservationRequestNotification obj = new ObservationRequestNotification();
        obj.init((Long) row.get("id"), (Boolean) row.get("is_active"), recevrId, recevrName, senderId,
                sender.fetchFullName(), (String) row.get("payload"));

        return obj;
    }

    private void markNotificationInactive(Long id, JdbcTemplate jdbcTemplate) {
        String query = "UPDATE" + Constants.TABLE_NOTIFICATIONS + "SET is_active = ? WHERE id = ?;";
        jdbcTemplate.update(query, false, id);
    }

    @Override
    public ResponseEntity<Object> add(Notification notification, User usr, Long receiverId, JdbcTemplate jdbcTemplate)
            throws DataAccessException {
        if (checkIfAlreadyPresent(notification, usr.getCostCenterId(), usr.getBusinessUnitId(), usr.getId(),
                jdbcTemplate)) {
            return new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
        }
        String query = "INSERT INTO" + Constants.TABLE_NOTIFICATIONS + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        java.util.Date date = new java.util.Date();
        String dateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(date);
        String timeStr = (new SimpleDateFormat("hh:mm:ss")).format(date);
        jdbcTemplate.update(query, usr.getCostCenterId(), usr.getBusinessUnitId(), receiverId, usr.getId(),
                notification.fetchPayload(), notification.getType(), dateStr, timeStr, true);

        return new ResponseEntity<>(Constants.MSG_UPDATED_SUCCESSFULLY, HttpStatus.CREATED);
    }

    private Boolean checkIfAlreadyPresent(Notification notification, Integer integer, Integer integer2, Long senderId,
            JdbcTemplate jdbcTemplate) {
        String query = "SELECT id FROM " + Constants.TABLE_NOTIFICATIONS
                + "WHERE cost_center_id = ? AND business_unit_id = ? AND sender_id = ? AND payload = ? AND type = ?";
        return jdbcTemplate
                .queryForList(query, integer, integer2, senderId, notification.fetchPayload(), notification.getType())
                .size() > 0;
    }
}

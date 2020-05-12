package com.klarite.backend.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.klarite.backend.Constants;
import com.klarite.backend.Privileges;
import com.klarite.backend.dto.Notification.*;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.AdminService;
import com.klarite.backend.service.NotificationService;
import com.klarite.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Object> respond(Long id, Long userId, Boolean accepted, String comment, JdbcTemplate jdbcTemplate) {
        try {
            ObservationRequestNotification notification = getRequestNotification(id, jdbcTemplate);
            User owner = userService.getUser(notification.getRequesterId(), false, jdbcTemplate);

            ObservationResponseNotification orn = new ObservationResponseNotification();
            String payload = orn.fetchPayload();
            java.util.Date date = new java.util.Date();
            String dateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(date);
            String timeStr = (new SimpleDateFormat("hh:mm:ss")).format(date);
            String query = "INSERT INTO" + Constants.TABLE_NOTIFICATIONS + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
            jdbcTemplate.update(query, owner.getCostCenterId(), owner.getBusinessUnitId(), owner.getId(),
                userId, payload, NotificationType.ObservationResponse, dateStr, timeStr, true);
            if (accepted) {
                markNotificationInactive(id, jdbcTemplate);
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
            if (Privileges.skillObserver.contains(usr.getRole())) {
                notifications.addAll(getRequestedObservations(usr, getActive, jdbcTemplate));
            }
            notifications.addAll(getObservationResponses(usr, getActive, jdbcTemplate));
            notifications.addAll(getValidationNotification(usr, getActive, jdbcTemplate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifications;
    }

    private List<Notification> getValidationNotification(User usr, Boolean getActive, JdbcTemplate jdbcTemplate) {
        List<Notification> notifications = new ArrayList<>();
        int onlyGetActive = getActive ? 1 : 0;
        String query = "SELECT * FROM " + Constants.TABLE_NOTIFICATIONS +
                " WHERE business_unit_id = ? and cost_center_id = ? and type = ?";

        List<Map<String, Object>> rows;
        if (getActive) {
            query = query + " and " + Constants.TABLE_NOTIFICATIONS.trim() + ".is_active = ?";
            rows = jdbcTemplate.queryForList(query, usr.getBusinessUnitId(), usr.getCostCenterId(), NotificationType.SkillValidation, onlyGetActive);
        }else {
            rows = jdbcTemplate.queryForList(query, usr.getBusinessUnitId(), usr.getCostCenterId(), NotificationType.SkillValidation);
        }
        for (Map<String, Object> row : rows) {
            SkillValidationNotification obj = new SkillValidationNotification();

            User requester = userService.getUser((Long) row.get("sender_id"), jdbcTemplate);
            obj.setId((Long) row.get("id"));
            obj.setActive((Boolean) row.get("is_active"));
            obj.setRequesterName(requester.getFirstName() + " " + requester.getLastName());
            obj.setRequesterId(requester.getId());
            obj.parseJSONString((String) row.get("payload"));
            obj.setSkillName(adminService.getSkill(obj.getSkillId(), jdbcTemplate).getSkillName());

            notifications.add(obj);
        }

        return notifications;
    }

    private List<Notification> getRequestedObservations(User usr, Boolean getActive, JdbcTemplate jdbcTemplate) {
        List<Notification> notifications = new ArrayList<>();
        int onlyGetActive = getActive ? 1 : 0;
        String query = "SELECT * FROM " + Constants.TABLE_NOTIFICATIONS + 
                       " WHERE business_unit_id = ? and cost_center_id = ? and type = ?";
        
        List<Map<String, Object>> rows;
        if (getActive) {
            query = query + " and " + Constants.TABLE_NOTIFICATIONS.trim() + ".is_active = ?";
            rows = jdbcTemplate.queryForList(query, usr.getBusinessUnitId(), usr.getCostCenterId(), NotificationType.ObservationRequest, onlyGetActive);
        }else {
            rows = jdbcTemplate.queryForList(query, usr.getBusinessUnitId(), usr.getCostCenterId(), NotificationType.ObservationRequest);
        }
        for (Map<String, Object> row : rows) {
            ObservationRequestNotification obj = new ObservationRequestNotification();

            User requester = userService.getUser((Long) row.get("sender_id"), jdbcTemplate);
            obj.setId((Long) row.get("id"));
            obj.setActive((Boolean) row.get("is_active"));
            obj.setRequesterName(requester.getFirstName() + " " + requester.getLastName());
            obj.setRequesterId(requester.getId());
            obj.parseJSONString((String) row.get("payload"));
            obj.setSkillName(adminService.getSkill(obj.getSkillId(), jdbcTemplate).getSkillName());

            notifications.add(obj);
        }

        return notifications;
    }

    private List<Notification> getObservationResponses(User usr, Boolean getActive, JdbcTemplate jdbcTemplate) {
        List<Notification> notifications = new ArrayList<>();
        int onlyGetActive = getActive ? 1 : 0;
        String query = "SELECT * FROM " + Constants.TABLE_NOTIFICATIONS + 
                       " WHERE receiver_id = ? and type = ?";
        
        List<Map<String, Object>> rows;
        if (getActive) {
            query = query + " and " + Constants.TABLE_NOTIFICATIONS.trim() + ".is_active = ?";
            rows = jdbcTemplate.queryForList(query, usr.getId(), NotificationType.ObservationResponse, onlyGetActive);
        }else {
            rows = jdbcTemplate.queryForList(query, usr.getId(), NotificationType.ObservationResponse);
        }
        for (Map<String, Object> row : rows) {
            ObservationResponseNotification obj = new ObservationResponseNotification();

            obj.setId((Long) row.get("id"));
            obj.setActive((Boolean) row.get("is_active"));
            obj.setObserverId((Long) row.get("sender_id"));
            User observer = userService.getUser(obj.getObserverId(), jdbcTemplate);
            obj.setObserverName(observer.getFirstName() + " " + observer.getLastName());
            obj.setRequesterId(usr.getId());
            obj.setRequesterName(usr.getFirstName() + " " + usr.getLastName());
            obj.parseJSONString((String) row.get("payload"));
            obj.setSkillName(adminService.getSkill(obj.getSkillId(), jdbcTemplate).getSkillName());

            notifications.add(obj);
        }

        return notifications;
    }

    private ObservationRequestNotification getRequestNotification(Long id, JdbcTemplate jdbcTemplate) {
        
        String query = "SELECT * FROM " + Constants.TABLE_NOTIFICATIONS + " WHERE id = ?";
        Map<String, Object> row = jdbcTemplate.queryForMap(query, id);
        
        ObservationRequestNotification obj = new ObservationRequestNotification();

        User requester = userService.getUser((Long) row.get("sender_id"), jdbcTemplate);
        obj.setId((Long) row.get("id"));
        obj.setActive((Boolean) row.get("is_active"));
        obj.setRequesterName(requester.getFirstName() + " " + requester.getLastName());
        obj.setRequesterId(requester.getId());
        obj.parseJSONString((String) row.get("payload"));
        obj.setSkillName(adminService.getSkill(obj.getSkillId(), jdbcTemplate).getSkillName());

        return obj;
    }

    private void markNotificationInactive(Long id, JdbcTemplate jdbcTemplate) {
        String query = "UPDATE" + Constants.TABLE_NOTIFICATIONS + "SET is_active = ? WHERE id = ?;";
        jdbcTemplate.update(query, false, id);
    }
}

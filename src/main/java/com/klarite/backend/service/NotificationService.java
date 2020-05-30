package com.klarite.backend.service;

import com.klarite.backend.dto.ReadNotification;
import com.klarite.backend.dto.User;
import com.klarite.backend.dto.Notification.Notification;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

public interface NotificationService {
    List<Notification> get(Long userId, Boolean getActive, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> add(Notification notification, User usr, Long receiverId, JdbcTemplate jdbcTemplate) throws DataAccessException;
    ResponseEntity<Object> delete(Long id, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> respond(Long id, Long userId, Boolean accepted, String comment, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> markRead(ReadNotification ids, JdbcTemplate jdbcTemplate);
}

package com.klarite.backend.service;

import com.klarite.backend.dto.Notification.Notification;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

public interface NotificationService {
    List<Notification> get(Long userId, Boolean getActive, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> delete(Long id, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> respond(Long id, Long userId, Boolean accepted, String comment, JdbcTemplate jdbcTemplate);
}

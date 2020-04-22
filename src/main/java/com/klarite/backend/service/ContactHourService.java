package com.klarite.backend.service;

import com.klarite.backend.dto.ContinuedEducation;
import com.klarite.backend.dto.ContinuedEducationEvents;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public interface ContactHourService {
    ContinuedEducationEvents getAll(Long userId, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> add(ContinuedEducation ce, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> edit(ContinuedEducation ce, JdbcTemplate jdbcTemplate);
}

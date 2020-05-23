package com.klarite.backend.service;

import com.klarite.backend.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public interface ContactHourService {
    ContinuedEducationEvents getAll(Long userId, JdbcTemplate jdbcTemplate);

    ContinuedEducation get(Long id, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> add(ContinuedEducation ce, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> edit(ContinuedEducation ce, JdbcTemplate jdbcTemplate);

    List<Certification> getAllCertifications(JdbcTemplate jdbcTemplate);

    List<CeReport> getCeReport(Integer businessUnitId, Integer costCenterId, JdbcTemplate jdbcTemplate);
}

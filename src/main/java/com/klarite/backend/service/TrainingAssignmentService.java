package com.klarite.backend.service;

import com.klarite.backend.dto.TrainingAssignment;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TrainingAssignmentService {
    List<TrainingAssignment> getAllAssignedTrainings(Long userId, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> deleteAssignment(Long id, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> addTrainingAssignment(TrainingAssignment trainingAssignment, JdbcTemplate jdbcTemplate);
}

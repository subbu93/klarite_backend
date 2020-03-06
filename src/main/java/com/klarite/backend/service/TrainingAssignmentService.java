package com.klarite.backend.service;

import com.klarite.backend.dto.TrainingAssignment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TrainingAssignmentService {
    List<TrainingAssignment> getAllAssignedTrainings(long userId, JdbcTemplate jdbcTemplate);
}

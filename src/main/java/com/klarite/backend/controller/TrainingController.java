package com.klarite.backend.controller;

import com.klarite.backend.dto.TrainingAssignment;
import com.klarite.backend.service.TrainingAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TrainingController {
    @Autowired
    private TrainingAssignmentService trainingAssignmentService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/assign_training/get_assigned_trainings")
    public List<TrainingAssignment> getAllAssignedTrainings(@RequestParam(required = false, value = "id") Long userId) {
        return trainingAssignmentService.getAllAssignedTrainings(userId, jdbcTemplate);
    }

    @DeleteMapping("/assign_training/delete_assigned_training")
    public ResponseEntity<Object> deleteAssignment(@RequestParam(value = "id") Long id) {
        return trainingAssignmentService.deleteAssignment(id, jdbcTemplate);
    }

    @PostMapping("/assign_training/add_assigned_training")
    public ResponseEntity<Object> addTrainingAssignment(@RequestBody TrainingAssignment trainingAssignment) {
        return trainingAssignmentService.addTrainingAssignment(trainingAssignment, jdbcTemplate);
    }
}

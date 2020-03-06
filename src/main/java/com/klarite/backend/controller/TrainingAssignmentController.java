package com.klarite.backend.controller;

import com.klarite.backend.dto.SkillAssignment;
import com.klarite.backend.dto.TrainingAssignment;
import com.klarite.backend.service.SkillAssignmentService;
import com.klarite.backend.service.TrainingAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TrainingAssignmentController {
    @Autowired
    private TrainingAssignmentService trainingAssignmentService;
    @Autowired
    JdbcTemplate jdbcTemplate;

        @GetMapping("/assign_training/get_assigned_trainings")
    public List<TrainingAssignment> getAllAssignedTrainings(@RequestParam(value = "id") long userId) {
        return trainingAssignmentService.getAllAssignedTrainings(userId, jdbcTemplate);
    }
}

package com.klarite.backend.service;

import com.klarite.backend.dto.ContactHours;
import com.klarite.backend.dto.Skill;
import com.klarite.backend.dto.Training;
import com.klarite.backend.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdminService {
    ResponseEntity<Object> addSkill(Skill skill, JdbcTemplate jdbcTemplate);

    List<Skill> getAllSkills(JdbcTemplate jdbcTemplate);

    Skill getSkill(long id, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> deleteSkill(long id, JdbcTemplate jdbcTemplate);

    List<Training> getAllTrainings(JdbcTemplate jdbcTemplate);

    Training getTraining(long trainingId, JdbcTemplate jdbcTemplate);

    List<User> getTrainer(JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> addTraining(Training training, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> deleteTraining(long trainingId, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> addContactHours(ContactHours ce, JdbcTemplate jdbcTemplate);

    ContactHours getCeHrs(String state, Integer certificationId, JdbcTemplate jdbcTemplate);
}

package com.klarite.backend.service;

import com.klarite.backend.dto.Skill;
import com.klarite.backend.dto.Training;
import com.klarite.backend.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdminSkillService {
    ResponseEntity<Object> addSkill(Skill skill, JdbcTemplate jdbcTemplate);

    List<Skill> getAllSkills(JdbcTemplate jdbcTemplate);

    Skill getSkill(long id, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> deleteSkill(long id, JdbcTemplate jdbcTemplate);

    List<Training> getAllTrainings(JdbcTemplate jdbcTemplate);

    Training getTraining(long trainingId, JdbcTemplate jdbcTemplate);

    List<User> getTrainer(JdbcTemplate jdbcTemplate);
}

package com.klarite.backend.service;

import com.klarite.backend.dto.Skill;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdminSkillService {
    List<Skill> getAllSkills(JdbcTemplate jdbcTemplate);
    Skill getSkill(long id, JdbcTemplate jdbcTemplate);
}

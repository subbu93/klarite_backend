package com.klarite.backend.service.impl;

import com.klarite.backend.dto.Skill;
import com.klarite.backend.service.AdminSkillService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AdminSkillServiceImpl implements AdminSkillService {
    @Override
    public List<Skill> getAllSkills(JdbcTemplate jdbcTemplate) {
        String query = "SELECT sk.id " +
                "      ,sk.name" +
                "      ,sk.description" +
                "      ,sk.threshold" +
                "      ,t.name as Training_prerequisite" +
                "  FROM skills as sk, trainings as t" +
                "  where t.id = sk.training_prerequisite_id";
        List<Skill> skills = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);

        for (Map row : rows) {
            Skill obj = new Skill();

            obj.setId(((Long) row.get("id")));
            obj.setName((String) row.get("name"));
            obj.setDescription((String) row.get("description"));
            obj.setThreshold((Integer) row.get("threshold"));
            obj.setSkillPreRequisite((String) row.get("Training_prerequisite"));
            skills.add(obj);
        }
//        return new ArrayList<Skill>(Collections.singleton(new Skill()));
        return skills;
    }

    @Override
    public Skill getSkill(long id, JdbcTemplate jdbcTemplate) {
        String query = "SELECT sk.id " +
                "      ,sk.name" +
                "      ,sk.description" +
                "      ,sk.threshold" +
                "      ,t.name as Training_prerequisite" +
                "     FROM skills as sk, trainings as t" +
                "     where sk.id = "+ id +" and " +
                "      t.id = sk.training_prerequisite_id";

        Map<String, Object> row = jdbcTemplate.queryForMap(query);

        Skill skill = new Skill();

        skill.setId(((Long) row.get("id")));
        skill.setName((String) row.get("name"));
        skill.setDescription((String) row.get("description"));
        skill.setThreshold((Integer) row.get("threshold"));
        skill.setSkillPreRequisite((String) row.get("Training_prerequisite"));
        return skill;
    }
}

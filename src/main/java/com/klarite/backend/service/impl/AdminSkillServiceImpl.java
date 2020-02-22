package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.Skill;
import com.klarite.backend.service.AdminSkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                "  FROM "+ Constants.TABLE_SKILLS + " as sk, "+Constants.TABLE_TRAININGS+" as t" +
                "  where t.id = sk.training_prerequisite_id " +
                "  AND sk.soft_delete = 0";
        List<Skill> skills = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);

        for (Map row : rows) {
            Skill obj = new Skill();

            obj.setId(((Long) row.get("id")));
            obj.setName((String) row.get("name"));
            obj.setDescription((String) row.get("description"));
            obj.setThreshold((Integer) row.get("threshold"));
            obj.setSkillTrainingPreRequisite((String) row.get("Training_prerequisite"));
            skills.add(obj);
        }
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
                "     WHERE sk.id = "+ id +" and " +
                "      t.id = sk.training_prerequisite_id" +
                "      AND sk.soft_delete = 0";

        Map<String, Object> row = jdbcTemplate.queryForMap(query);

        Skill skill = new Skill();

        skill.setId(((Long) row.get("id")));
        skill.setName((String) row.get("name"));
        skill.setDescription((String) row.get("description"));
        skill.setThreshold((Integer) row.get("threshold"));
        skill.setSkillTrainingPreRequisite((String) row.get("Training_prerequisite"));
        return skill;
    }

    @Override
    public ResponseEntity<Object> addSkill(Skill skill, JdbcTemplate jdbcTemplate) {
        String query = "INSERT INTO "+ Constants.TABLE_SKILLS +
                " VALUES      (?," +
                "             ?, " +
                "             ?," +
                "             ?); ";

        try{
            jdbcTemplate.update(query,skill.getName(), skill.getDescription(), skill.getThreshold(),
                    skill.getSkillTrainingPreRequisite());
            return new ResponseEntity<>("Stored", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> deleteSkill(long skillId, JdbcTemplate jdbcTemplate) {
        String query = "UPDATE " + Constants.TABLE_SKILLS +
                " SET soft_delete = 1 " +
                " WHERE id = ?";
        try{
            jdbcTemplate.update(query,skillId);
            return new ResponseEntity<>("Deleted Skill", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

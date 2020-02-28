package com.klarite.backend.service.impl;

import com.klarite.backend.dto.SkillAssignment;
import com.klarite.backend.service.SkillAssignedService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SkillAssignedServiceImpl implements SkillAssignedService {

    @Override
    public List<SkillAssignment> getAllAssignedSkills(long userId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT t2.assignment_name   AS assignment_name, " +
                "       t2.completion_date  AS completion_date, " +
                "       t2.user_id, " +
                "       t2.skill_id as skill_id, "+
                "       t2.cost_center_name AS cost_center_name, " +
                "       skills.NAME         AS skills_name, " +
                "       skills.threshold AS skill_threshold " +
                "FROM   (SELECT t1.assignment_name  AS assignment_name, " +
                "               t1.completion_date AS completion_date, " +
                "               t1.user_id, " +
                "               cc.NAME            AS cost_center_name, " +
                "               t1.skill_id " +
                "        FROM   (SELECT sa.NAME            AS assignment_name, " +
                "                       sa.completion_date AS completion_date, " +
                "                       sa.user_id, " +
                "                       u.cost_center_id, " +
                "                       sa.skill_id " +
                "                FROM   skill_assignments AS sa " +
                "                       INNER JOIN users AS u " +
                "                               ON sa.user_id = u.id) AS t1 " +
                "               JOIN cost_center AS cc " +
                "                 ON t1.cost_center_id = cc.id) AS t2 " +
                "       INNER JOIN skills " +
                "               ON t2.skill_id = skills.id " +
                "WHERE  t2.user_id = " + userId;

        List<SkillAssignment> skillAssignments = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);

        for (Map row : rows) {
            SkillAssignment obj = new SkillAssignment();

            obj.setAssignedSkill(((String) row.get("assignment_name")));
            obj.setName((String) row.get("skills_name"));
            obj.setCostCenterName((String) row.get("cost_center_name"));
            obj.setCompletionDate((Date) row.get("completion_date"));
            obj.setEpisodeCount(getEpisodeCount((Long) row.get("skill_id"), userId, jdbcTemplate));
            obj.setSkillThreshold((Integer) row.get("skill_threshold"));

            skillAssignments.add(obj);
        }
        return skillAssignments;
    }

    Integer getEpisodeCount(long skillId, long userId, JdbcTemplate jdbcTemplate ) {
        String query = "SELECT Count(*) AS count " +
                "FROM   skill_episodes " +
                "WHERE  user_id = ? " +
                "       AND skill_id = ? ";

        Map<String, Object> row = jdbcTemplate.queryForMap(query, userId, skillId);
        return (Integer) row.get("count");
    }
}

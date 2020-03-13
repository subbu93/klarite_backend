package com.klarite.backend.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.klarite.backend.dto.SkillEpisodes;
import com.klarite.backend.service.SkillService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl implements SkillService {
    @Override
    public List<SkillEpisodes> getAllEpisodes(Long userId, Long skillId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * FROM   skill_episodes WHERE  user_id = $user AND skill_id = $skill";
        query = query.replace("$user", userId.toString()).replace("$skill", skillId.toString());

        List<SkillEpisodes> skillEpisodes = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        for (Map<String, Object> row : rows) {
            SkillEpisodes obj = new SkillEpisodes();

            obj.setUserId(userId);
            obj.setSkillId(skillId);
            obj.setDate((Date) row.get("date"));
            obj.setMrn((String) row.get("mrn"));
            obj.setObserved((boolean) row.get("is_observed"));
            obj.setObserverId(((Long) row.get("observer_id")));
            obj.setAudited((boolean) row.get("is_audited"));

            skillEpisodes.add(obj);
        }
        return skillEpisodes;
    }
}

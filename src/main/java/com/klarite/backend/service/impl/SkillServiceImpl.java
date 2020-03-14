package com.klarite.backend.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.klarite.backend.dto.SkillEpisode;
import com.klarite.backend.dto.SkillEpisodes;
import com.klarite.backend.service.SkillService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl implements SkillService {
    @Override
    public List<SkillEpisodes> getAllEpisodes(Long userId, Long skillId, JdbcTemplate jdbcTemplate) {
        String query = "SELECT * FROM episodes WHERE user_id = $user";
        query = query.replace("$user", userId.toString());

        List<SkillEpisodes> skillEpisodes = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        for (Map<String, Object> row : rows) {
            
            Long episode_id = (Long) row.get("id");

            query = "SELECT * FROM skill_episodes WHERE episode_id = $e_id  AND skill_id = $skill";
            query = query.replace("$e_id", episode_id.toString()).replace("$skill", skillId.toString());
            List<Map<String, Object>> new_rows = jdbcTemplate.queryForList(query);

            if (new_rows.size() > 0) {
                SkillEpisodes obj = new SkillEpisodes();
                obj.setId(episode_id);
                obj.setUserId((Long) row.get("user_id"));
                obj.setDate((Date) row.get("date"));
                obj.setMrn((String) row.get("mrn"));
                obj.setAudited((boolean) row.get("is_audited"));
                for (Map<String, Object> new_row : new_rows) {
                    SkillEpisode skillEpisode = new SkillEpisode();
                    skillEpisode.setSkillId((Long) new_row.get("skill_id"));
                    skillEpisode.setObserved((boolean) new_row.get("is_observed"));
                    skillEpisode.setObserverId((Long) new_row.get("observer_id"));
                    obj.getEpisodes().add(skillEpisode);
                }
                skillEpisodes.add(obj);
            }
        }
        return skillEpisodes;
    }
}

package com.klarite.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.klarite.backend.dto.SkillEpisode;
import com.klarite.backend.dto.SkillEpisodes;
import com.klarite.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public ResponseEntity<Object> addSkillEpisodes(SkillEpisodes skillEpisodes, JdbcTemplate jdbcTemplate) {
        try{
            String query = "INSERT INTO episodes VALUES(?, ?, ?, ?); SELECT SCOPE_IDENTITY() as id;";
            Map<String, Object> row = jdbcTemplate.queryForMap(query, skillEpisodes.getUserId(), skillEpisodes.getDate(), 
                skillEpisodes.getMrn(), skillEpisodes.isAudited());
            BigDecimal episodeId = (BigDecimal) row.get("id");
            insertSkillEpisodes(episodeId.longValue(), skillEpisodes.getEpisodes(), jdbcTemplate);
            return new ResponseEntity<>("Stored", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    private void insertSkillEpisodes(long episodeId, List<SkillEpisode> skillEpisodeList, JdbcTemplate jdbcTemplate) {
        String query = "INSERT INTO skill_episodes VALUES(?, ?, ?, ?);";
        for (SkillEpisode skillEpisode : skillEpisodeList) {
            jdbcTemplate.update(query,episodeId,skillEpisode.getSkillId(), skillEpisode.isObserved(), skillEpisode.getObserverId());
        }
    }
}

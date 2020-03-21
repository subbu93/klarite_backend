package com.klarite.backend.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.SkillEpisode;
import com.klarite.backend.dto.Episode;
import com.klarite.backend.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public ResponseEntity<Object> addSkillEpisode(Episode skillEpisodes, JdbcTemplate jdbcTemplate) {
        try{
            String query = "INSERT INTO" + Constants.TABLE_EPISODES + "VALUES(?, ?, ?, ?); SELECT SCOPE_IDENTITY() as id;";
            Map<String, Object> row = jdbcTemplate.queryForMap(query, skillEpisodes.getUserId(), skillEpisodes.getDate(), 
                skillEpisodes.getMrn(), skillEpisodes.isAudited());
            BigDecimal episodeId = (BigDecimal) row.get("id");
            insertSkillEpisodes(episodeId.longValue(), skillEpisodes.getEpisodes(), jdbcTemplate);
            return new ResponseEntity<>("Stored", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> updateSkillEpisode(Episode episode, JdbcTemplate jdbcTemplate) {
        String query = "UPDATE" + Constants.TABLE_EPISODES + "SET date = ?, mrn = ?, is_audited = ? WHERE id = ?;";
        try {
            jdbcTemplate.update(query, episode.getDate(), episode.getMrn(), episode.isAudited(), episode.getId());
            insertSkillEpisodes(episode.getId().longValue(), episode.getEpisodes(), jdbcTemplate);
            return new ResponseEntity<>("Updated", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private void insertSkillEpisodes(long episodeId, List<SkillEpisode> skillEpisodeList, JdbcTemplate jdbcTemplate) {
        deleteExisitingEPisodes(episodeId, jdbcTemplate);
        
        String query = "INSERT INTO" + Constants.TABLE_SKILL_EPISODES + "VALUES(?, ?, ?, ?);";
        for (SkillEpisode skillEpisode : skillEpisodeList) {
            jdbcTemplate.update(query, episodeId, skillEpisode.getSkillId(), skillEpisode.isObserved(), skillEpisode.getObserverId());
        }
    }

    private void deleteExisitingEPisodes(long episodeId, JdbcTemplate jdbcTemplate) {
        String query = "DELETE FROM" + Constants.TABLE_SKILL_EPISODES + "where episode_id = ?;";
        jdbcTemplate.update(query, episodeId);
    }
}

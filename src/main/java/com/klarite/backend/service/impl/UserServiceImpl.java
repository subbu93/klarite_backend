package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.Episode;
import com.klarite.backend.dto.SkillEpisode;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<User> getAllUsers(JdbcTemplate jdbcTemplate) {
        String query = "SELECT u.*, " +
                "       b.NAME as business_unit, " +
                "       c.NAME as cost_center" +
                " FROM   "+Constants.TABLE_USERS+" AS u, " +
                "       "+Constants.TABLE_COST_CENTER+" AS c, " +
                "       "+ Constants.TABLE_BUSINESS_UNIT +" AS b " +
                " WHERE  u.cost_center_id = c.id " +
                "       AND u.business_unit_id = b.id ";
        List<User> users;
        try {
            users = new ArrayList<>();
            List<Map<String, Object>> userRows = jdbcTemplate.queryForList(query);
            for (Map<String, Object> row : userRows) {
                User user = new User();

                user.setId(((Long) row.get("id")));
                user.setOusId((String) row.get("osu_id"));
                user.setFirstName((String) row.get("first_name"));
                user.setMiddleName((String) row.get("middle_name"));
                user.setLastName((String) row.get("last_name"));
                user.setEmail((String) row.get("email"));
                user.setBusinessUnitId((Integer) row.get("business_unit_id"));
                user.setCostCenterId((Integer) row.get("cost_center_id"));
                user.setBusinessUnitName((String) row.get("business_unit"));
                user.setCostCenterName((String) row.get("cost_center"));
                user.setUrl((String) row.get("image_url"));
                user.setTrainer(true);

                users.add(user);
            }
            return users;
        } catch (Exception e) {
            return new ArrayList<>();
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

package com.klarite.backend.service.impl;

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
        String query = "INSERT INTO skill_episodes " +
                "VALUES      (?," +
                "             ?, " +
                "             ?, " +
                "             ?, " +
                "             ?," +
                "             ?," +
                "             ?); ";

        try{
            jdbcTemplate.update(query,skillEpisodes.getSkillId(),skillEpisodes.getUserId(),
                    skillEpisodes.getDate(), skillEpisodes.getMrn(), skillEpisodes.isObserved(),
                    skillEpisodes.getObserverId(), skillEpisodes.isAudited());
            return new ResponseEntity<>("Stored", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}

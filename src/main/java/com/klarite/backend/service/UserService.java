package com.klarite.backend.service;

import com.klarite.backend.dto.Episode;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public interface UserService {
    ResponseEntity<Object> addSkillEpisode(Episode skillEpisodes, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> updateSkillEpisode(Episode skillEpisodes, JdbcTemplate jdbcTemplate);
}

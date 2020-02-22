package com.klarite.backend.service;

import com.klarite.backend.dto.SkillEpisodes;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

public interface UserService {
    ResponseEntity<Object> addSkillEpisodes(SkillEpisodes skillEpisodes, JdbcTemplate jdbcTemplate);
}

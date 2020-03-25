package com.klarite.backend.service;

import com.klarite.backend.dto.Episode;
import com.klarite.backend.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface UserService {
    ResponseEntity<Object> addSkillEpisode(Episode skillEpisodes, JdbcTemplate jdbcTemplate);
    ResponseEntity<Object> updateSkillEpisode(Episode skillEpisodes, JdbcTemplate jdbcTemplate);

    List<User> getAllUsers(JdbcTemplate jdbcTemplate);
}

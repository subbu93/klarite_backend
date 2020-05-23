package com.klarite.backend.service;

import com.klarite.backend.dto.Episode;
import com.klarite.backend.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface UserService {
    ResponseEntity<Object> addSkillEpisode(Episode skillEpisodes, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> updateSkillEpisode(Episode skillEpisodes, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> markAttendance(String uuid, Long userId, JdbcTemplate jdbcTemplate);

    List<User> getAllUsers(JdbcTemplate jdbcTemplate);

    User getUser(Long userId, JdbcTemplate jdbcTemplate);

    User getUser(Long userId, Boolean getImageData, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> addUser(User user, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> deleteUser(Long userId, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> updateProfilePic(Long userId, String imageData, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> changePswd(Long userId, String oldPswd, String newPswd, JdbcTemplate jdbcTemplate);
}

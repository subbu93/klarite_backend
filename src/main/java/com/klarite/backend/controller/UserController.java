package com.klarite.backend.controller;

import com.klarite.backend.dto.SkillEpisodes;
import com.klarite.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/user_services/add_episode")
    public ResponseEntity<Object> addEpisodes(@RequestBody SkillEpisodes skillEpisodes) {
        return userService.addSkillEpisodes(skillEpisodes, jdbcTemplate);
    }
}

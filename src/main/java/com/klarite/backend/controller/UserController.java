package com.klarite.backend.controller;

import com.klarite.backend.dto.Episode;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/user_services/get_all_users")
    public List<User> getAllUsers(){
        return userService.getAllUsers(jdbcTemplate);
    }

    @PostMapping("/user_services/add_episode")
    public ResponseEntity<Object> addEpisode(@RequestBody Episode skillEpisodes) {
        return userService.addSkillEpisode(skillEpisodes, jdbcTemplate);
    }

    @PostMapping("/user_services/update_episode")
    public ResponseEntity<Object> updateEpisode(@RequestBody Episode skillEpisodes) {
        return userService.updateSkillEpisode(skillEpisodes, jdbcTemplate);
    }

    @PostMapping("/user_services/mark_attendance")
    public ResponseEntity<Object> updateEpisode(@RequestParam(value = "uuid") String uuid,
                                                    @RequestParam(value = "userId") Long userId) {
        return userService.markAttendance(uuid, userId, jdbcTemplate);
    }
}

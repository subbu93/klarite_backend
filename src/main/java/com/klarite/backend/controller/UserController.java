package com.klarite.backend.controller;

import com.klarite.backend.dto.Episode;
import com.klarite.backend.dto.ProfileImage;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.AuthenticationService;
import com.klarite.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/user_services/get_all_users")
    public List<User> getAllUsers() {
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
    public ResponseEntity<Object> markAttendance(@RequestParam(value = "uuid") String uuid,
                                                 @RequestParam(value = "userId") Long userId) {
        return userService.markAttendance(uuid, userId, jdbcTemplate);
    }

    @PostMapping("/user_services/add_user")
    public ResponseEntity<Object> addUser(@RequestHeader(value = "token") String token,
                                          @RequestBody User user) {
        if (authenticationService.isTokenValid(token, jdbcTemplate)) {
            return userService.addUser(user, jdbcTemplate);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/user_services/delete_user")
    public ResponseEntity<Object> deleteUser(@RequestParam(value = "id") Long userId) {
        return userService.deleteUser(userId, jdbcTemplate);
    }

    @GetMapping("/user_services/get_user_data")
    public User getUserData(@RequestParam(value = "userId") Long userId) {
        return userService.getUser(userId, true, jdbcTemplate);
    }

    @PostMapping("/user_services/update_picture")
    public ResponseEntity<Object> updateProfilePic(@RequestHeader(value = "token") String token,
                                                   @RequestBody ProfileImage data) {
        if (authenticationService.isTokenValid(token, jdbcTemplate)) {
            return userService.updateProfilePic(data.getUserId(), data.getImageData(), jdbcTemplate);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/user_services/change_password")
    public ResponseEntity<Object> changePswd(@RequestHeader(value = "token") String token,
                                             @RequestParam(value = "userId") Long userId,
                                             @RequestParam(value = "oldPswd") String oldPswd,
                                             @RequestParam(value = "newPswd") String newPswd) {
        if (authenticationService.isTokenValid(token, jdbcTemplate)) {
            return userService.changePswd(userId, oldPswd, newPswd, jdbcTemplate);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

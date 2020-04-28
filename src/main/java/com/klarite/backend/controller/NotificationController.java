package com.klarite.backend.controller;

import java.util.ArrayList;
import java.util.List;

import com.klarite.backend.dto.Notification.Notification;
import com.klarite.backend.service.AuthenticationService;
import com.klarite.backend.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/notification/get")
    public List<Notification> get(//@RequestHeader(value = "token") String token,
                                      @RequestParam(value = "userId") Long userId, 
                                      @RequestParam(value = "getActive") Boolean getActive) {
        //if (authenticationService.isTokenValid(token, jdbcTemplate)) {
            return notificationService.get(userId, getActive, jdbcTemplate);
        // } else {
        //     return new ArrayList<>();
        // }
    }

    @PostMapping("/notification/respond")
    public ResponseEntity<Object> respond(//@RequestHeader(value = "token") String token,
                                      @RequestParam(value = "id") Long id, 
                                      @RequestParam(value = "userId") Long userId,
                                      @RequestParam(value = "action") Boolean accepted,
                                      @RequestParam(value = "comment") String comment) {
        //if (authenticationService.isTokenValid(token, jdbcTemplate)) {
            return notificationService.respond(id, userId, accepted, comment, jdbcTemplate);
        // } else {
        //     return new ArrayList<>();
        // }
    }

    @DeleteMapping("/notification/delete")
    public ResponseEntity<Object> delete(//@RequestHeader(value = "token") String token,
                                      @RequestParam(value = "id") Long id) {
        //if (authenticationService.isTokenValid(token, jdbcTemplate)) {
            return notificationService.delete(id, jdbcTemplate);
        // } else {
        //     return new ArrayList<>();
        // }
    }
}
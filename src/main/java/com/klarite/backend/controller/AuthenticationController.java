package com.klarite.backend.controller;

import com.klarite.backend.dto.Login;
import com.klarite.backend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Login loginDetails) {
        return authenticationService.login(loginDetails.getUsername(), loginDetails.getPassword(), jdbcTemplate);
    }
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader(value = "token") String token) {
        return authenticationService.logout(token, jdbcTemplate);
    }
}

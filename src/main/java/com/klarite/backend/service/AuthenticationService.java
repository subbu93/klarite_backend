package com.klarite.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public interface AuthenticationService {
    ResponseEntity<Object> login(String userName, String password, JdbcTemplate jdbcTemplate);

    Boolean isTokenValid(String token, JdbcTemplate jdbcTemplate);

    ResponseEntity<Object> logout(String token, JdbcTemplate jdbcTemplate);
}

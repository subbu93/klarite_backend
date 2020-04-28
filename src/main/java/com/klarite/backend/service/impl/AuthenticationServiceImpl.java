package com.klarite.backend.service.impl;

import com.klarite.backend.Constants;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.AuthenticationService;
import com.klarite.backend.service.UserService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public ResponseEntity<Object> login(String userName, String password, JdbcTemplate jdbcTemplate) {
        String query = "SELECT id AS userId " +
                " FROM " + Constants.TABLE_USERS +
                " WHERE  email = ? " +
                "       AND password = ?" +
                "       AND soft_delete = 0;";
        User user;
        ResponseEntity<Object> response;
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(query, userName, password);
            UserService userService = new UserServiceImpl();
            user = userService.getUser((Long) row.get("userId"), jdbcTemplate);
            UUID token = generateToken(jdbcTemplate);
            user.setToken(token.toString());
            response = new ResponseEntity<>(user, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @Override
    public Boolean isTokenValid(String token, JdbcTemplate jdbcTemplate) {
        String query = "SELECT count(1) AS count FROM " + Constants.TABLE_TOKENS +
                "WHERE token = ? ;";

        Map<String, Object> row = jdbcTemplate.queryForMap(query, token);
        return row.get("count").equals(1);
    }

    @Override
    public ResponseEntity<Object> logout(String token, JdbcTemplate jdbcTemplate) {
        String deleteQuery = "DELETE FROM " + Constants.TABLE_TOKENS + " WHERE token = ?";

        jdbcTemplate.update(deleteQuery, token);

        return new ResponseEntity<>("Token Deleted", HttpStatus.OK);
    }


    private UUID generateToken(JdbcTemplate jdbcTemplate) {
        UUID token = UUID.randomUUID();
        String saveTokenQuery = "INSERT INTO " + Constants.TABLE_TOKENS +
                " VALUES (?); ";
        jdbcTemplate.update(saveTokenQuery, token);
        return token;
    }
}

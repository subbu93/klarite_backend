package com.klarite.backend.controller;

import com.klarite.backend.dto.ContinuedEducation;
import com.klarite.backend.dto.ContinuedEducationEvents;
import com.klarite.backend.service.ContactHourService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ContactHourController {
    @Autowired
    private ContactHourService contactHourService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/ce/get_all")
    public ContinuedEducationEvents getAll(@RequestParam(value = "userId") long userId) {
        return contactHourService.getAll(userId, jdbcTemplate);
    }

    @PostMapping("/ce/add")
    public ResponseEntity<Object> add(@RequestBody ContinuedEducation ce) {
        return contactHourService.add(ce, jdbcTemplate);
    }
}
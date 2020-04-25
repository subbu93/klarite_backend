package com.klarite.backend.controller;

import com.klarite.backend.dto.ContinuedEducation;
import com.klarite.backend.dto.ContinuedEducationEvents;
import com.klarite.backend.service.ContactHourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("/ce/get")
    public ContinuedEducation get(@RequestParam(value = "id") long id) {
        return contactHourService.get(id, jdbcTemplate);
    }

    @PostMapping("/ce/add")
    public ResponseEntity<Object> add(@RequestBody ContinuedEducation ce) {
        return contactHourService.add(ce, jdbcTemplate);
    }

    @PostMapping("/ce/edit")
    public ResponseEntity<Object> edit(@RequestBody ContinuedEducation ce) {
        return contactHourService.edit(ce, jdbcTemplate);
    }
}
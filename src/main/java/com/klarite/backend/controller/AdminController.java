package com.klarite.backend.controller;

import com.klarite.backend.dto.ContactHours;
import com.klarite.backend.dto.Skill;
import com.klarite.backend.dto.Training;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.AdminService;
import com.klarite.backend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private AuthenticationService authenticationService;

    /*
    Skills APIs
    */
    @GetMapping("/skill_admin/get_all_skills")
    public List<Skill> getAllSkills(@RequestHeader(value = "token") String token) {
        if (authenticationService.isTokenValid(token, jdbcTemplate)) {
            return adminService.getAllSkills(jdbcTemplate);
        } else {
            return new ArrayList<>();
        }
    }

    @GetMapping("/skill_admin/get_skill")
    public Skill getSkill(@RequestParam(value = "id") long id) {
        return adminService.getSkill(id, jdbcTemplate);
    }

    @PostMapping("/skill_admin/add_skill")
    public ResponseEntity<Object> addSkill(@RequestBody Skill skill) {
        return adminService.addSkill(skill, jdbcTemplate);
    }

    @DeleteMapping("/skill_admin/delete_skill")
    public ResponseEntity<Object> deleteSkill(@RequestParam(value = "id") long id) {
        return adminService.deleteSkill(id, jdbcTemplate);
    }

    /*
    Training APIs
    */
    @GetMapping("/skill_admin/get_all_trainings")
    public List<Training> getAllTrainings() {
        return adminService.getAllTrainings(jdbcTemplate);
    }

    @GetMapping("/skill_admin/get_training")
    public Training getTraining(@RequestParam(value = "id") long trainingId) {
        return adminService.getTraining(trainingId, jdbcTemplate);
    }

    @GetMapping("/skill_admin/get_trainer")
    public List<User> getTrainer() {
        return adminService.getTrainer(jdbcTemplate);
    }

    @PostMapping("/skill_admin/add_training")
    public ResponseEntity<Object> addTraining(@RequestBody Training training) {
        return adminService.addTraining(training, jdbcTemplate);
    }

    @DeleteMapping("/skill_admin/delete_training")
    public ResponseEntity<Object> deleteTraining(@RequestParam(value = "id") long id) {
        return adminService.deleteTraining(id, jdbcTemplate);
    }

    @PostMapping("/skill_admin/add_ce")
    public ResponseEntity<Object> addCe(@RequestBody ContactHours ce) {
        return adminService.addContactHours(ce, jdbcTemplate);
    }

    @GetMapping("/skill_admin/get_ce_hrs")
    public ContactHours getCeHrs(@RequestParam(value = "state") String state,
                                 @RequestParam(value = "title") String title,
                                 @RequestParam(value = "pos") String position) {
        return adminService.getCeHrs(state, title, position, jdbcTemplate);
    }
}

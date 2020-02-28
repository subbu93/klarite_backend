package com.klarite.backend.controller;

import com.klarite.backend.dto.Skill;
import com.klarite.backend.dto.Training;
import com.klarite.backend.dto.User;
import com.klarite.backend.service.AdminSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AdminSkillController {
    @Autowired
    private AdminSkillService adminSkillService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/skill_admin/get_all_skills")
    public List<Skill> getAllSkills() {
        return adminSkillService.getAllSkills(jdbcTemplate);
    }

    @GetMapping("/skill_admin/get_skill")
    public Skill getSkill(@RequestParam(value = "id") long id){
        return adminSkillService.getSkill(id, jdbcTemplate);
    }

    @PostMapping("/skill_admin/add_skill")
    public ResponseEntity<Object> addSkill(@RequestBody Skill skill) {
        return adminSkillService.addSkill(skill, jdbcTemplate);
    }

    @DeleteMapping("/skill_admin/delete_skill")
    public ResponseEntity<Object> deleteSkill(@RequestParam(value = "id") long id) {
        return adminSkillService.deleteSkill(id, jdbcTemplate);
    }

    @GetMapping("/skill_admin/get_all_trainings")
    public List<Training> getAllTrainings() {
        return adminSkillService.getAllTrainings(jdbcTemplate);
    }

    @GetMapping("/skill_admin/get_training")
    public Training getTraining(@RequestParam(value = "id") long trainingId) {
        return adminSkillService.getTraining(trainingId,jdbcTemplate);
    }

    @GetMapping("/skill_admin/get_trainer")
    public List<User> getTrainer() {
        return adminSkillService.getTrainer(jdbcTemplate);
    }
}

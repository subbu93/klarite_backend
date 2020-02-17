package com.klarite.backend.controller;

import com.klarite.backend.dto.Skill;
import com.klarite.backend.service.AdminSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

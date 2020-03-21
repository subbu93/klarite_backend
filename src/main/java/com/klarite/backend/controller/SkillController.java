package com.klarite.backend.controller;

import com.klarite.backend.dto.Episode;
import com.klarite.backend.service.SkillService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class SkillController {
    @Autowired
    private SkillService skillService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/skill/get_all_episodes")
    public List<Episode> getAllEpisodes(@RequestParam(value = "userId") long userId,
                                              @RequestParam(value = "skillId") long skillId) {
        return skillService.getAllEpisodes(userId, skillId, jdbcTemplate);
    }

    @GetMapping("/skill/get_episode")
    public Episode getEpisode(@RequestParam(value = "id") long id) {
        return skillService.getEpisode(id, jdbcTemplate);
    }
}
package com.klarite.backend.service;

import java.util.List;

import com.klarite.backend.dto.SkillEpisodes;
import org.springframework.jdbc.core.JdbcTemplate;

public interface SkillService {
    List<SkillEpisodes> getAllEpisodes(Long userId, Long skillId, JdbcTemplate jdbcTemplate);
}

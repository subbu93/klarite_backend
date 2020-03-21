package com.klarite.backend.service;

import java.util.List;

import com.klarite.backend.dto.Episode;
import org.springframework.jdbc.core.JdbcTemplate;

public interface SkillService {
    List<Episode> getAllEpisodes(Long userId, Long skillId, JdbcTemplate jdbcTemplate);
    Episode getEpisode(Long episodeId, JdbcTemplate jdbcTemplate);
}

package com.klarite.backend.dto.Notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ObservationRequestNotification extends Notification {
    
    private static final String EPISODE_ID_KEY = "episodeId";
    private static final String SKILLS_KEY = "skills";
    
    private Long episodeId;
    private Set<Long> skills;
    private String skillNames;

    @Override
    public int getType() {
        return NotificationType.ObservationRequest;
    }

    @Override
    public String fetchPayload() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        try {
            map.put(EPISODE_ID_KEY, episodeId.toString());
            map.put(SKILLS_KEY, mapper.writeValueAsString(skills));
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void parseJSONString(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> map = mapper.readValue(payload, new TypeReference<Map<String, String>>(){});
            setEpisodeId(Long.parseLong(map.get(EPISODE_ID_KEY)));
            setSkills(mapper.readValue((String) map.get(SKILLS_KEY), new TypeReference<Set<Long>>(){}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public void addSkillId(long skillId) {
        if (skills == null)
            skills = new HashSet<>();

        skills.add(skillId);
    }

    public Set<Long> getSkills() {
        return skills;
    }

    public void setSkills(Set<Long> skills) {
        this.skills = skills;
    }

    public String getSkillNames() {
        return skillNames;
    }

    public void setSkillNames(String skillNames) {
        this.skillNames = skillNames;
    }
}
package com.klarite.backend.dto.Notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SkillValidationNotification extends Notification {
    private Long skillId;
    private Long episodeId;
    private String comment;
    private Boolean isValidated;
    private String skillName;

    @Override
    public int getType() {
        return NotificationType.SkillValidation;
    }

    @Override
    public String fetchPayload() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        try {
            map.put("skillId", skillId.toString());
            map.put("episodeId", episodeId.toString());
            map.put("comment", comment);
            map.put("isValidated", isValidated.toString());

            // convert map to JSON string
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
            // convert JSON string to Map
            Map<String, String> map = mapper.readValue(payload, new TypeReference<Map<String, String>>() {});
            setSkillId(Long.parseLong(map.get("skillId")));
            setEpisodeId(Long.parseLong(map.get("episodeId")));
            setComment(map.get("comment"));
            setValidated(Boolean.parseBoolean(map.get("isValidated")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getValidated() {
        return isValidated;
    }

    public void setValidated(Boolean validated) {
        isValidated = validated;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}

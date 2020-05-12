package com.klarite.backend.dto.Notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObservationRequestNotification extends Notification {
    private Long skillId;
    private String skillName;
    private String requesterName;
    private Long requesterId;

    @Override
    public int getType() {
        return NotificationType.ObservationResponse;
    }

    @Override
    public String fetchPayload() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        try {
            map.put("skillId", skillId.toString());
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
            Map<String, String> map = mapper.readValue(payload, new TypeReference<Map<String, String>>(){});
            setSkillId(Long.parseLong(map.get("skillId")));
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

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }
}
package com.klarite.backend.dto.Notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObservationResponseNotification extends Notification {
    private Long skillId;
    private String skillName;
    private Long requesterId;
    private String requesterName;
    private Long observerId;
    private String observerName;
    private String comment;
    private Boolean isAccepted;

    @Override
    public int getType() {
        return NotificationType.ObservationResponse;    
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

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public Long getObserverId() {
        return observerId;
    }

    public void setObserverId(Long observerId) {
        this.observerId = observerId;
    }

    public String getObserverName() {
        return observerName;
    }

    public void setObserverName(String observerName) {
        this.observerName = observerName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String description) {
        this.comment = description;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public static String getPayload(Long skillId, String comment, Boolean isAccepted) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        try {
            map.put("skillId", skillId.toString());
            map.put("comment", comment);
            map.put("isAccepted", isAccepted.toString());
            // convert map to JSON string
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void parseJSONString(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // convert JSON string to Map
            Map<String, String> map = mapper.readValue(payload, new TypeReference<Map<String, String>>(){});
            setSkillId(Long.parseLong(map.get("skillId")));
            setComment(map.get("comment"));
            setIsAccepted(Boolean.parseBoolean(map.get("isAccepted")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
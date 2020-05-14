package com.klarite.backend.dto.Notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpcomingTrainingNotification extends Notification {

    private static final String TRAINING_ASSIGN_ID_KEY = "trainingAssignmentId";

    private Long trainingAssignId;
    private String traningName;
    private String location;
    private String timeStamp;

    @Override
    public int getType() {
        return NotificationType.SkillValidation;
    }

    @Override
    public String fetchPayload() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        try {
            map.put(TRAINING_ASSIGN_ID_KEY, trainingAssignId.toString());
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
            setTrainingAssignmentId(Long.parseLong(map.get(TRAINING_ASSIGN_ID_KEY)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getTrainingAssignmentId() {
        return trainingAssignId;
    }

    public void setTrainingAssignmentId(Long trainingId) {
        this.trainingAssignId = trainingId;
    }

    public String getTraningName() {
        return traningName;
    }

    public void setTraningName(String traningName) {
        this.traningName = traningName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
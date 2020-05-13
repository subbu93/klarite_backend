package com.klarite.backend.dto.Notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObservationResponseNotification extends Notification {

    private static final String EPISODE_ID_KEY = "episodeId";
    private static final String COMMENT_KEY = "comment";
    private static final String IS_ACCEPTED_KEY = "isAccepted";

    private Long episodeId;
    private String comment;
    private Boolean isAccepted;

    @Override
    public int getType() {
        return NotificationType.ObservationResponse;
    }

    @Override
    public String fetchPayload() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        try {
            map.put(EPISODE_ID_KEY, episodeId.toString());
            map.put(COMMENT_KEY, comment);
            map.put(IS_ACCEPTED_KEY, isAccepted.toString());
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
            setComment(map.get(COMMENT_KEY));
            setIsAccepted(Boolean.parseBoolean(map.get(IS_ACCEPTED_KEY)));
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
}
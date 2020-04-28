package com.klarite.backend.dto.Notification;

public class ObservationRequestNotification extends Notification {
    private Long skillId;
    private String skillName;
    private String requesterName;
    private Long requesterId;
    private final String type = "ObservationRequest";

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

    public String getType() {
        return type;
    }
}
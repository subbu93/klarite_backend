package com.klarite.backend.dto;

public class SkillEpisode {
    private Long skillId;
    private boolean isObserved;
    private Long observerId = null;
    private String skillName;
    private boolean isValidated;
    private boolean isRemediated;
    private String comment;
    
    public long getSkillId() {
        return skillId;
    }

    public void setSkillId(long skillId) {
        this.skillId = skillId;
    }

    public boolean isObserved() {
        return isObserved;
    }

    public void setObserved(boolean isObserved) {
        this.isObserved = isObserved;
    }

    public Long getObserverId() {
        return observerId;
    }

    public void setObserverId(Long observerId) {
        this.observerId = observerId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean isValidated) {
        this.isValidated = isValidated;
    }

    public boolean isRemediated() {
        return isRemediated;
    }

    public void setRemediated(boolean isRemediated) {
        this.isRemediated = isRemediated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
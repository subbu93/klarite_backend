package com.klarite.backend.dto;

public class SkillEpisode {
    private long skillId;
    private boolean isObserved;
    private Long observerId = null;

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
}
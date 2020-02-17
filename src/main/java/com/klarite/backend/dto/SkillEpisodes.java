package com.klarite.backend.dto;

import java.util.Date;

public class SkillEpisodes {
    private long id;
    private int skillId;
    private int userId;
    private Date date;
    private String mrn;
    private boolean isObserved;
    private int observerId;
    private boolean isAudited;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public boolean isObserved() {
        return isObserved;
    }

    public void setObserved(boolean observed) {
        isObserved = observed;
    }

    public int getObserverId() {
        return observerId;
    }

    public void setObserverId(int observerId) {
        this.observerId = observerId;
    }

    public boolean isAudited() {
        return isAudited;
    }

    public void setAudited(boolean audited) {
        isAudited = audited;
    }
}

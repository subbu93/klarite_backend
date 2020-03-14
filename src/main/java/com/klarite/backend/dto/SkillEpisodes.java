package com.klarite.backend.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SkillEpisodes {
    private long id;
    private long userId;
    private Date date;
    private String mrn;
    private boolean isAudited;
    private List<SkillEpisode> episodes = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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

    public boolean isAudited() {
        return isAudited;
    }

    public void setAudited(boolean isAudited) {
        this.isAudited = isAudited;
    }

    public List<SkillEpisode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<SkillEpisode> episodes) {
        this.episodes = episodes;
    }
}

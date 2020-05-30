package com.klarite.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class Episode {
    private long id;
    private long userId;
    private String date;
    private String mrn;
    private List<SkillEpisode> episodes = new ArrayList<>();

    public Long getId() {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMrn() {
        return mrn;
    }

    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    public List<SkillEpisode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<SkillEpisode> episodes) {
        this.episodes = episodes;
    }
}

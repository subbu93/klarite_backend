package com.klarite.backend.dto;

public class Skill {
    private Long id;
    private String name;
    private String description;
    private Integer threshold;
    private String skillPreRequisite;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public String getSkillPreRequisite() {
        return skillPreRequisite;
    }

    public void setSkillPreRequisite(String skillPreRequisite) {
        this.skillPreRequisite = skillPreRequisite;
    }
}

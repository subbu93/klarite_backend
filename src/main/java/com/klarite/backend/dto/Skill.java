package com.klarite.backend.dto;

public class Skill {
    private Long id;
    private String skillName;
    private String description;
    private Integer threshold;
    private String skillTrainingPreRequisite;

    private Long trainingId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
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

    public String getSkillTrainingPreRequisite() {
        return skillTrainingPreRequisite;
    }

    public void setSkillTrainingPreRequisite(String skillTrainingPreRequisite) {
        this.skillTrainingPreRequisite = skillTrainingPreRequisite;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

}

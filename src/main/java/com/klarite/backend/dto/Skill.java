package com.klarite.backend.dto;

public class Skill {
    private Long id;
    private String skillName;
    private String description;
    private Integer totalThreshold;
    private String skillTrainingPreRequisite;
    private Integer episodeCount;
    private Integer validatedEpisodeCount;
    private Long trainingId;
    private Integer validationThreshold;


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

    public Integer getTotalThreshold() {
        return totalThreshold;
    }

    public void setTotalThreshold(Integer threshold) {
        this.totalThreshold = threshold;
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

    public Integer getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(Integer episodeCount) {
        this.episodeCount = episodeCount;
    }

    public Integer getValidationThreshold() {
        return validationThreshold;
    }

    public void setValidationThreshold(Integer validationThreshold) {
        this.validationThreshold = validationThreshold;
    }

    public Integer getValidatedEpisodeCount() {
        return validatedEpisodeCount;
    }

    public void setValidatedEpisodeCount(Integer validatedEpisodeCount) {
        this.validatedEpisodeCount = validatedEpisodeCount;
    }
}

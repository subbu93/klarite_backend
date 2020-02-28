package com.klarite.backend.dto;

import java.util.Date;
import java.util.List;
public class SkillAssignment {
    private String name;
    private String costCenterName;
    private String AssignedSkill;
    private String SkillValidatedBy;
    private Date CompletionDate;
    private Integer episodeCount;
    private Integer skillThreshold;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public String getAssignedSkill() {
        return AssignedSkill;
    }

    public void setAssignedSkill(String assignedSkill) {
        AssignedSkill = assignedSkill;
    }

    public String getSkillValidatedBy() {
        return SkillValidatedBy;
    }

    public void setSkillValidatedBy(String skillValidatedBy) {
        SkillValidatedBy = skillValidatedBy;
    }

    public Date getCompletionDate() {
        return CompletionDate;
    }

    public void setCompletionDate(Date completionDate) {
        CompletionDate = completionDate;
    }

    public Integer getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(Integer episodeCount) {
        this.episodeCount = episodeCount;
    }

    public Integer getSkillThreshold() {
        return skillThreshold;
    }

    public void setSkillThreshold(Integer skillThreshold) {
        this.skillThreshold = skillThreshold;
    }
}

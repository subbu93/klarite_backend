package com.klarite.backend.dto;

import java.util.Date;
public class SkillAssignment {

    private Long skillId;
    private Long userId;
    private String skillAssignmentName;
    private String costCenterName;
    private String AssignedSkill;
    private String SkillValidatedBy;
    private Date CompletionDate;
    private Integer episodeCount;
    private Integer skillThreshold;

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSkillAssignmentName() {
        return skillAssignmentName;
    }

    public void setSkillAssignmentName(String name) {
        this.skillAssignmentName = name;
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

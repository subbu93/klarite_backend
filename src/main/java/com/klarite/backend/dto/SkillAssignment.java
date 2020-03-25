package com.klarite.backend.dto;

import java.util.Date;
import java.util.List;

public class SkillAssignment {

    private Long assignmentId;
    private Long skillId;
    private List<Long> userIds;
    private String skillAssignmentName;
    private String costCenterName;
    private Integer costCenterId;
    private String AssignedSkill;
    private Long skillValidatorId;
    private Date CompletionDate;
    private Integer episodeCount;
    private Integer skillThreshold;

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
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

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Integer getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(Integer costCenterId) {
        this.costCenterId = costCenterId;
    }

    public Long getSkillValidatorId() {
        return skillValidatorId;
    }

    public void setSkillValidatorId(Long skillValidatorId) {
        this.skillValidatorId = skillValidatorId;
    }
}

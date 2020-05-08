package com.klarite.backend.dto;

import java.util.Date;
import java.util.List;

public class SkillAssignment {

    private Long assignmentId;
    private Long skillId;
    private List<Long> assignedUserIds;
    private String skillAssignmentName;
    private String costCenterName;
    private Integer costCenterId;
    private String businessUnitName;
    private Integer businessUnitId;
    private String AssignedSkill;
    private Long skillValidatorId;
    private Date CompletionDate;
    private Integer skillThreshold;

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public List<Long> getAssignedUserIds() {
        return assignedUserIds;
    }

    public void setAssignedUserIds(List<Long> assignedUserIds) {
        this.assignedUserIds = assignedUserIds;
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

    public String getBusinessUnitName() {
        return businessUnitName;
    }

    public void setBusinessUnitName(String businessUnitName) {
        this.businessUnitName = businessUnitName;
    }

    public Integer getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(Integer businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public Long getSkillValidatorId() {
        return skillValidatorId;
    }

    public void setSkillValidatorId(Long skillValidatorId) {
        this.skillValidatorId = skillValidatorId;
    }
}

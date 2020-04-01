package com.klarite.backend.dto;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class TrainingAssignment {
    private String trainingAssignmentName;
    private Long assignmentId;
    private String businessUnit;
    private Integer businessUnitId;
    private String costCenter;
    private Integer costCenterId;
    private Long trainingId;
    private String trainingName;
    private Date date;
    private Time startTime;
    private String location;
    private List<Long> assignedUserIds;
    private List<String> assignedUserName;
    private String uuid;

    public String getTrainingAssignmentName() {
        return trainingAssignmentName;
    }

    public void setTrainingAssignmentName(String trainingAssignmentName) {
        this.trainingAssignmentName = trainingAssignmentName;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;   
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Long> getAssignedUserIds() {
        return assignedUserIds;
    }

    public void setAssignedUserIds(List<Long> assignedUserIds) {
        this.assignedUserIds = assignedUserIds;
    }

    public List<String> getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(List<String> assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public Integer getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(Integer businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public Integer getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(Integer costCenterId) {
        this.costCenterId = costCenterId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

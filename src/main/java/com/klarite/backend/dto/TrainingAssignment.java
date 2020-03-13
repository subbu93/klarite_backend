package com.klarite.backend.dto;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class TrainingAssignment {
    private String businessUnit;
    private String costCenter;
    private Long trainingId;
    private String trainingName;
    private Date date;
    private Time startTime;
    private String location;
    private List<Long> assignedUserId;
    private List<String> assignedUserName;


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

    public List<Long> getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(List<Long> assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public List<String> getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(List<String> assignedUserName) {
        this.assignedUserName = assignedUserName;
    }
}

package com.klarite.backend.dto;

public class ContactHours {
    private Long id;
    private String state;
    private String userTitle;
    private String position;
    private Integer ceHrs;
    private Integer timePeriod;
    private Integer certificationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getCeHrs() {
        return ceHrs;
    }

    public void setCeHrs(Integer ceHrs) {
        this.ceHrs = ceHrs;
    }

    public Integer getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(Integer timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Integer getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Integer certificationId) {
        this.certificationId = certificationId;
    }
}

package com.klarite.backend.dto;

public class ContactHours {
    private Long id;
    private String state;
    private String userTitle;
    private String position;
    private Integer ceHrsPerYear;

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

    public Integer getCeHrsPerYear() {
        return ceHrsPerYear;
    }

    public void setCeHrsPerYear(Integer ceHrsPerYear) {
        this.ceHrsPerYear = ceHrsPerYear;
    }
}

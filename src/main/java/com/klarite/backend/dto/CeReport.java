package com.klarite.backend.dto;

public class CeReport {
    private User user;
    private Float totalHrs;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Float getTotalHrs() {
        return totalHrs;
    }

    public void setTotalHrs(Float totalHrs) {
        this.totalHrs = totalHrs;
    }
}

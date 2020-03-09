package com.klarite.backend.dto;

public class User {
    private long id;
    private String ousId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Integer costCenterId;
    private String costCenterName;
    private Integer businessUnitId;
    private String businessUnitName;
    private String url;
    private boolean isTrainer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOusId() {
        return ousId;
    }

    public void setOusId(String ousId) {
        this.ousId = ousId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(Integer costCenterId) {
        this.costCenterId = costCenterId;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public Integer getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(Integer businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public String getBusinessUnitName() {
        return businessUnitName;
    }

    public void setBusinessUnitName(String businessUnitName) {
        this.businessUnitName = businessUnitName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isTrainer() {
        return isTrainer;
    }

    public void setTrainer(boolean trainer) {
        isTrainer = trainer;
    }
}

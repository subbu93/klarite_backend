package com.klarite.backend.dto;

import java.util.List;

public class ContinuedEducationEvents {
    private Integer ceHrsPerYear;
    List<Training> trainings;
    List<ContinuedEducation> education;

    public Integer getCeHrsPerYear() {
        return ceHrsPerYear;
    }

    public void setCeHrsPerYear(Integer ceHrsPerYear) {
        this.ceHrsPerYear = ceHrsPerYear;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    public List<ContinuedEducation> getEducation() {
        return education;
    }

    public void setEducation(List<ContinuedEducation> education) {
        this.education = education;
    }
}

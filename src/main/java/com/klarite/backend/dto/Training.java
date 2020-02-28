package com.klarite.backend.dto;

public class Training {
    private Long id;
    private String trainingName;
    private String description;
    private boolean isCE;
    private Long ceId;
    private String trainerName;
    private long trainerId;
    private int totalHours;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCE() {
        return isCE;
    }

    public void setCE(boolean CE) {
        isCE = CE;
    }

    public Long getCeId() {
        return ceId;
    }

    public void setCeId(Long ceId) {
        this.ceId = ceId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(long trainerId) {
        this.trainerId = trainerId;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }
}

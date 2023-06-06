package com.example.hourswork_project;


public class Work {

    Integer id;  // Represents the ID of the work
    Long startDate;  // Represents the start date of the work
    Long endDate;  // Represents the end date of the work

    // Constructor with ID, start date, and end date
    public Work(Integer id, Long startDate, Long endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
    }

    // Constructor with only start date and end date
    public Work(Long startDate, Long endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getter for the start date
    public long getStartDate() {
        return startDate;
    }

    // Setter for the start date
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    // Getter for the end date
    public Long getEndDate() {
        return endDate;
    }

    // Setter for the end date
    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    // Getter for the ID
    public Integer getId() {
        return id;
    }

    // Setter for the ID
    public void setId(Integer id) {
        this.id = id;
    }
}



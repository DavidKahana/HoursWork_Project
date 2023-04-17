package com.example.hourswork_project;

public class Work {

    Long startDate;
    Long endDate;
    Integer id;

    public Work(Long startDate, Long endDate, Integer id) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
    }
    public Work(Long startDate, Long endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}


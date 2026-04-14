package com.example.phasmatic.data.model;

public class MasterCourse {
    private String id;
    private String masterId;
    private String name;
    private String teacher;
    private Double ects;
    private String description;
    private Double cost;

    public MasterCourse(String id, String masterId, String name, String teacher, Double ects, String description, Double cost) {
        this.id = id;
        this.masterId = masterId;
        this.name = name;
        this.teacher = teacher;
        this.ects = ects;
        this.description = description;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setCost(Double c){this.cost = c;}
    public Double getCost(){return cost;}
    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Double getEcts() {
        return ects;
    }

    public void setEcts(Double ects) {
        this.ects = ects;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

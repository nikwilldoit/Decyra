package com.example.phasmatic.data.model;

public class Note {
    private String id;
    private String title;
    private String description;
    private long createdTime;

    public Note() {
    }

    public Note(String id, String title, String description, long createdTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
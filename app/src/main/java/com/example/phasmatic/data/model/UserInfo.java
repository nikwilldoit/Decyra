package com.example.phasmatic.data.model;

public class UserInfo {
    private String userId;
    private String university;
    private String academicLevel;
    private String languages;
    private String gpa;
    private String field;
    private Double budgetPerYear;
    private Integer yearOfStudies;
    private String advisorType;
    private String advisorImage;

    public UserInfo() {
    }

    public UserInfo(String userId,
                    String university,
                    String academicLevel,
                    String languages,
                    String gpa,
                    String field,
                    Double budgetPerYear,
                    Integer yearOfStudies,
                    String advisorType,
                    String advisorImage) {
        this.userId = userId;
        this.university = university;
        this.academicLevel = academicLevel;
        this.languages = languages;
        this.gpa = gpa;
        this.field = field;
        this.budgetPerYear = budgetPerYear;
        this.yearOfStudies = yearOfStudies;
        this.advisorType = advisorType;
        this.advisorImage = advisorImage;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(String academicLevel) { this.academicLevel = academicLevel; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public Double getBudgetPerYear() { return budgetPerYear; }
    public void setBudgetPerYear(Double budgetPerYear) { this.budgetPerYear = budgetPerYear; }

    public Integer getYearOfStudies() { return yearOfStudies; }
    public void setYearOfStudies(Integer yearOfStudies) { this.yearOfStudies = yearOfStudies; }

    public String getAdvisorType() { return advisorType; }
    public void setAdvisorType(String advisorType) { this.advisorType = advisorType; }

    public String getAdvisorImage() { return advisorImage; }
    public void setAdvisorImage(String advisorImage) { this.advisorImage = advisorImage; }
}
package com.example.phasmatic.data.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
//    private List<Double> faceEmbedding;

    private String profileImageUrl;

    public User(){
    }

    public User(String id, String fullName, String email, String password, String phoneNumber) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public User(String id, String fullName, String email,
                String password, String phoneNumber, String profileImageUrl) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
    }

    private boolean gray = false;

    public boolean isGray() {
        return gray;
    }

    public void setGray(boolean gray) {
        this.gray = gray;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

//    public List<Double> getFaceEmbedding() {
//        return faceEmbedding;
//    }
//
//    public void setFaceEmbedding(List<Double> faceEmbedding) {
//        this.faceEmbedding = faceEmbedding;
//    }
}
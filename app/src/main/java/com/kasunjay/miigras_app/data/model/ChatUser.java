package com.kasunjay.miigras_app.data.model;

import java.io.Serializable;

public class ChatUser implements Serializable {
    private Long employeeId;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String profilePic;
    private String jobType;
    private double latitude;
    private double longitude;
    private String fcmToken;

    public ChatUser() {
    }

    public ChatUser(Long employeeId, Long userId, String name, String email, String phone, String profilePic, String jobType, double latitude, double longitude, String fcmToken) {
        this.employeeId = employeeId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profilePic = profilePic;
        this.jobType = jobType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fcmToken = fcmToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

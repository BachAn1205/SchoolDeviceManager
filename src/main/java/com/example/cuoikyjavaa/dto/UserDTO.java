package com.example.cuoikyjavaa.dto;

public class UserDTO {
    private Integer userID;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;

    // Constructor mặc định (cần thiết cho các framework như Spring)
    public UserDTO() {}

    // Constructor đầy đủ tham số
    public UserDTO(Integer userID, String username, String fullName, String email, String phoneNumber, String role) {
        this.userID = userID;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // --- Getters và Setters cho tất cả các thuộc tính ---

    public Integer getUserID() {
        return userID;
    }
    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
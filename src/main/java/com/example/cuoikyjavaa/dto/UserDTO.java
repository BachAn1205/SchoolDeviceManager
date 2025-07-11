package com.example.cuoikyjavaa.dto;

public class UserDTO {
    private Integer userID;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;

    public UserDTO() {}

    public UserDTO(Integer userID, String username, String fullName, String email, String phone, String role) {
        this.userID = userID;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public Integer getUserID() { return userID; }
    public void setUserID(Integer userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
} 
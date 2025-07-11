package com.example.cuoikyjavaa.dto;

import com.example.cuoikyjavaa.dto.UserDTO; // Import model User

public class LoginResponse {
    private String message;
    private UserDTO user; // Đổi từ User sang UserDTO

    public LoginResponse(String message, UserDTO user) {
        this.message = message;
        this.user = user;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public UserDTO getUser() {
        return user;
    }

    // Setters (nếu cần, nhưng thường không cần cho response DTO)
    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
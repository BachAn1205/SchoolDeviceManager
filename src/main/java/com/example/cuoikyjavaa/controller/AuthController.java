package com.example.cuoikyjavaa.controller;

import com.example.cuoikyjavaa.dto.LoginResponse;
import com.example.cuoikyjavaa.dto.UserDTO;
import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; // THÊM DÒNG NÀY
import org.slf4j.LoggerFactory; // THÊM DÒNG NÀY

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // THÊM DÒNG NÀY

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        List<String> errors = userService.register(user);
        if (errors.isEmpty()) {
            return ResponseEntity.ok("Đăng ký thành công");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        logger.info("Attempting login for username: {}", user.getUsername());
        User foundUser = userService.login(user.getUsername(), user.getPassword());

        if (foundUser == null) {
            logger.warn("Login failed for username: {}. Incorrect credentials.", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
        }

        logger.info("Login successful for user: {} with actual role: {}", foundUser.getUsername(), foundUser.getRole());

        try {
            UserDTO userDTO = new UserDTO(foundUser.getUserID(), foundUser.getUsername(), foundUser.getFullName(), foundUser.getEmail(), foundUser.getPhone(), foundUser.getRole());
            LoginResponse response = new LoginResponse("Đăng nhập thành công", userDTO);
            logger.debug("Returning LoginResponse: message={}, user.username={}, user.role={}, user.fullName={}",
                    response.getMessage(), userDTO.getUsername(),
                    userDTO.getRole(), userDTO.getFullName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during LoginResponse serialization: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ khi xử lý phản hồi đăng nhập.");
        }
    }
}
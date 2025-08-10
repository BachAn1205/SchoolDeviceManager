package com.example.cuoikyjavaa.controller;

import com.example.cuoikyjavaa.dto.LoginResponse;
import com.example.cuoikyjavaa.dto.UserDTO;
import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Cho phép truy cập từ frontend (React/HTML)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    // Đăng ký tài khoản
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        List<String> errors = userService.register(user);
        if (errors.isEmpty()) {
            logger.info("Đăng ký thành công cho username: {}", user.getUsername());
            return ResponseEntity.ok("Đăng ký thành công");
        } else {
            logger.warn("Đăng ký thất bại cho username: {} với lỗi: {}", user.getUsername(), errors);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
        }
    }

    // Đăng nhập tài khoản
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        logger.info("Đăng nhập với username: {}", user.getUsername());

        User foundUser = userService.login(user.getUsername(), user.getPassword());

        if (foundUser == null) {
            logger.warn("Đăng nhập thất bại: Sai tài khoản hoặc mật khẩu cho username: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
        }

        // Chuyển dữ liệu sang DTO để bảo vệ dữ liệu nhạy cảm
        try {
            UserDTO userDTO = new UserDTO(
                    foundUser.getUserID(),
                    foundUser.getUsername(),
                    foundUser.getFullName(),
                    foundUser.getEmail(),
                    foundUser.getPhoneNumber(),
                    foundUser.getRole()
            );

            LoginResponse response = new LoginResponse("Đăng nhập thành công", userDTO);

            logger.info("Đăng nhập thành công: user={}, role={}, fullName={}",
                    userDTO.getUsername(), userDTO.getRole(), userDTO.getFullName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo LoginResponse: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi máy chủ khi xử lý phản hồi đăng nhập.");
        }
    }
}

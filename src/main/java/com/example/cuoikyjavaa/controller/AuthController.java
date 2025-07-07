package com.example.cuoikyjavaa.controller;

import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        boolean registered = userService.register(user);
        if (registered) {
            return ResponseEntity.ok("Đăng ký thành công");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên người dùng đã tồn tại");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User found = userService.login(user.getUsername(), user.getPassword());
        if (found == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
        }

        // Trả về thông tin JSON nếu đăng nhập thành công
        return ResponseEntity.ok(Map.of(
                "userID", found.getUserID(),
                "username", found.getUsername(),
                "role", found.getRole(),
                "fullName", found.getFullName()
        ));
    }
}

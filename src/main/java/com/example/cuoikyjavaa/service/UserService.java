package com.example.cuoikyjavaa.service;

import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired private UserRepository repo;
    @Autowired private PasswordEncoder encoder;

    /**
     * Đăng ký tài khoản. Trả về:
     * 0: Thành công
     * 1: Trùng username
     * 2: Trùng email
     * 3: Trùng phone
     */
    public List<String> register(User user) {
        List<String> errors = new ArrayList<>();
        if (repo.existsByUsername(user.getUsername())) errors.add("Tên người dùng đã tồn tại");
        if (repo.existsByEmail(user.getEmail())) errors.add("Email đã được sử dụng");
        if (repo.existsByPhone(user.getPhone())) errors.add("Số điện thoại đã được sử dụng");
        if (!errors.isEmpty()) return errors;
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return errors;
    }

    public User login(String username, String password) {
        Optional<User> optional = repo.findByUsername(username);
        if (optional.isPresent()) {
            User user = optional.get();
            if (encoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}

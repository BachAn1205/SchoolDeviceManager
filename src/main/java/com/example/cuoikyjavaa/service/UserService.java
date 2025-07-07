package com.example.cuoikyjavaa.service;

import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired private UserRepository repo;
    @Autowired private PasswordEncoder encoder;

    public boolean register(User user) {
        if (repo.existsByUsername(user.getUsername())) return false;
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return true;
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

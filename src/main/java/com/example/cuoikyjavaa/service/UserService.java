package com.example.cuoikyjavaa.service;

import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired private UserRepository repo;
    @Autowired private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user.getRole())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        String roleName = mapRoleToAuthority(role);
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    private String mapRoleToAuthority(String role) {
        switch (role) {
            case "Admin":
                return "ROLE_ADMIN";
            case "Giảng viên":
                return "ROLE_GIANGVIEN";
            case "Sinh viên":
                return "ROLE_SINHVIEN";
            case "Nhân viên thiết bị":
                return "ROLE_NHANVIEN";
            case "Kỹ thuật viên":
                return "ROLE_KYTHUATVIEN";
            default:
                return "ROLE_USER"; // Vai trò mặc định nếu không khớp
        }
    }

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
        if (repo.existsByPhoneNumber(user.getPhoneNumber())) errors.add("Số điện thoại đã được sử dụng");
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

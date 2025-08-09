package com.example.devicemanager.controller;

import com.example.cuoikyjavaa.dto.UserDTO;
import com.example.cuoikyjavaa.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(u -> new UserDTO(
                        u.getUserID(),
                        u.getUsername(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getRole()
                ))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@org.springframework.web.bind.annotation.PathVariable("id") Integer id, Model model) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/users";
        }
        UserDTO dto = new UserDTO(
                user.getUserID(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
        model.addAttribute("user", dto);
        return "admin/user_edit";
    }

    @org.springframework.web.bind.annotation.PostMapping("/users/edit")
    public String updateUser(@org.springframework.web.bind.annotation.ModelAttribute("user") UserDTO dto) {
        if (dto.getUserID() == null) return "redirect:/admin/users";
        var userOpt = userRepository.findById(dto.getUserID());
        if (userOpt.isEmpty()) return "redirect:/admin/users";
        var user = userOpt.get();
        // Không cho sửa username và password tại đây
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @org.springframework.web.bind.annotation.PostMapping("/users/delete/{id}")
    public String deleteUser(@org.springframework.web.bind.annotation.PathVariable("id") Integer id) {
        if (id != null && userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/equipments")
    public String equipments(Model model) {
        return "admin/equipments";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        return "admin/requests";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        return "admin/reports";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        return "admin/settings";
    }

    @GetMapping("/admin_profile")
    public String adminProfile(Model model) {
        return "admin/admin_profile";
    }
}

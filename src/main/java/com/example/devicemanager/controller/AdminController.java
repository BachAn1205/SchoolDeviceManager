package com.example.devicemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/users")
    public String users(Model model) {
        return "admin/users";
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

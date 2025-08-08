package com.example.devicemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nhanvien")
public class NhanVienController {

    @GetMapping("/request_equipment")
    public String requestEquipment(Model model) {
        return "nhanvien/request_equipment";
    }

    @GetMapping("/report_issue")
    public String reportIssue(Model model) {
        return "nhanvien/report_issue";
    }

    @GetMapping("/my_requests")
    public String myRequests(Model model) {
        return "nhanvien/my_requests";
    }

    @GetMapping("/staff_profile")
    public String staffProfile(Model model) {
        return "nhanvien/staff_profile";
    }
}

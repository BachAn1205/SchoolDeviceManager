package com.example.cuoikyjavaa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sinhvien")
public class SinhVienController {

    @GetMapping("/find_equipment")
    public String findEquipment(Model model) {
        return "sinhvien/find_equipment";
    }

    @GetMapping("/my_requests_student")
    public String myRequests(Model model) {
        return "sinhvien/my_requests_student";
    }

    @GetMapping("/borrow_history")
    public String borrowHistory(Model model) {
        return "sinhvien/borrow_history";
    }

    @GetMapping("/student_profile")
    public String studentProfile(Model model) {
        return "sinhvien/student_profile";
    }
}

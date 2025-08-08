package com.example.devicemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/giangvien")
public class GiangVienController {

    @GetMapping("/search_equipment")
    public String searchEquipment(Model model) {
        return "giangvien/search_equipment";
    }

    @GetMapping("/submit_borrow_request")
    public String submitBorrowRequest(Model model) {
        return "giangvien/submit_borrow_request";
    }

    @GetMapping("/my_requests")
    public String myRequests(Model model) {
        return "giangvien/my_requests";
    }

    @GetMapping("/borrow_history")
    public String borrowHistory(Model model) {
        return "giangvien/borrow_history";
    }

    @GetMapping("/lecturer_profile")
    public String lecturerProfile(Model model) {
        return "giangvien/lecturer_profile";
    }
}

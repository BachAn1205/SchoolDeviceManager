package com.example.cuoikyjavaa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kythuatvien")
public class KyThuatVienController {

    @GetMapping("/equipment_management")
    public String equipmentManagement(Model model) {
        // Match existing template file equipment_management_tech.html
        return "kythuatvien/equipment_management";
    }

    @GetMapping("/maintenance_schedule")
    public String maintenanceSchedule(Model model) {
        return "kythuatvien/maintenance_schedule";
    }

    @GetMapping("/repair_requests")
    public String repairRequests(Model model) {
        // Match existing template file repair_requests.html
        return "kythuatvien/repair_requests";
    }

    @GetMapping("/tech_reports")
    public String techReports(Model model) {
        // Match existing template file tech_reports.html
        return "kythuatvien/tech_reports";
    }
}

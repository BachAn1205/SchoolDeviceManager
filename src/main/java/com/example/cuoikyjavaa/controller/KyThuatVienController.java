package com.example.cuoikyjavaa.controller;

import com.example.cuoikyjavaa.model.BaoCaoSuCo;
import com.example.cuoikyjavaa.model.Equipment;
import com.example.cuoikyjavaa.repository.BaoCaoSuCoRepository;
import com.example.cuoikyjavaa.repository.EquipmentRepository;
import com.example.cuoikyjavaa.repository.LoaiThietBiRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/kythuatvien")
public class KyThuatVienController {

    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private BaoCaoSuCoRepository baoCaoSuCoRepository;
    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "kythuatvien/dashboard";
    }

    @GetMapping("/equipment_management")
    public String equipmentManagement(Model model,
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "loaiThietBiId", required = false) Integer loaiThietBiId,
                                      @RequestParam(value = "trangThai", required = false) String trangThai) {

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String searchStatus = (trangThai != null && !trangThai.isEmpty()) ? trangThai : null;

        List<Equipment> equipmentList = equipmentRepository.searchEquipments(searchKeyword, loaiThietBiId, searchStatus);

        model.addAttribute("equipments", equipmentList);
        model.addAttribute("loaiThietBiList", loaiThietBiRepository.findAll());
        model.addAttribute("trangThaiList", Arrays.asList("Sẵn sàng", "Đang sử dụng", "Đang bảo trì", "Hỏng"));

        // Pass back search params to keep them in the form
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedLoaiId", loaiThietBiId);
        model.addAttribute("selectedTrangThai", trangThai);

        return "kythuatvien/equipment_management";
    }

    @GetMapping("/maintenance_schedule")
    public String maintenanceSchedule(Model model) {
        return "kythuatvien/maintenance_schedule";
    }

    @GetMapping("/repair_requests")
    public String repairRequests(Model model) {
        model.addAttribute("repairRequests", baoCaoSuCoRepository.findAll());
        model.addAttribute("objectMapper", objectMapper);
        return "kythuatvien/repair_requests";
    }

    @PostMapping("/repair_requests/acknowledge/{id}")
    public String acknowledgeRepairRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        BaoCaoSuCo repairRequest = baoCaoSuCoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        if ("Cần xử lý".equals(repairRequest.getTrangThai())) {
            repairRequest.setTrangThai("Đang xử lý");

            Equipment equipment = repairRequest.getThietBi();
            equipment.setTrangThai("Đang bảo trì");

            baoCaoSuCoRepository.save(repairRequest);
            equipmentRepository.save(equipment);

            redirectAttributes.addFlashAttribute("message", "Đã tiếp nhận yêu cầu sửa chữa. Thiết bị đã được chuyển sang trạng thái 'Đang bảo trì'.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể tiếp nhận yêu cầu này.");
        }

        return "redirect:/kythuatvien/repair_requests";
    }

    @PostMapping("/repair_requests/complete/{id}")
    public String completeRepairRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        BaoCaoSuCo repairRequest = baoCaoSuCoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        if ("Đang xử lý".equals(repairRequest.getTrangThai())) {
            repairRequest.setTrangThai("Đã xử lý");

            Equipment equipment = repairRequest.getThietBi();
            equipment.setTrangThai("Sẵn sàng");

            baoCaoSuCoRepository.save(repairRequest);
            equipmentRepository.save(equipment);

            redirectAttributes.addFlashAttribute("message", "Đã hoàn thành sửa chữa. Thiết bị đã được chuyển về trạng thái 'Sẵn sàng'.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể hoàn thành yêu cầu này.");
        }

        return "redirect:/kythuatvien/repair_requests";
    }

    @GetMapping("/tech_reports")
    public String techReports() {
        return "kythuatvien/tech_reports";
    }
}

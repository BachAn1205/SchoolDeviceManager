package com.example.cuoikyjavaa.controller;

import com.example.cuoikyjavaa.dto.UserDTO;
import com.example.cuoikyjavaa.model.Equipment;
import com.example.cuoikyjavaa.model.YeuCauMuon;
import com.example.cuoikyjavaa.repository.EquipmentRepository;
import com.example.cuoikyjavaa.repository.LoaiThietBiRepository;
import com.example.cuoikyjavaa.repository.UserRepository;
import com.example.cuoikyjavaa.repository.YeuCauMuonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final LoaiThietBiRepository loaiThietBiRepository;
    private final YeuCauMuonRepository yeuCauMuonRepository;

    public AdminController(UserRepository userRepository,
                          EquipmentRepository equipmentRepository,
                          LoaiThietBiRepository loaiThietBiRepository,
                          YeuCauMuonRepository yeuCauMuonRepository) {
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.loaiThietBiRepository = loaiThietBiRepository;
        this.yeuCauMuonRepository = yeuCauMuonRepository;
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

    // ========== QUẢN LÝ THIẾT BỊ ==========
    
    @GetMapping("/equipments")
    public String listEquipments(
            Model model,
            @RequestParam(value = "page", required = false) Optional<Integer> page,
            @RequestParam(value = "size", required = false) Optional<Integer> size) {
        
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10); // 10 bản ghi mỗi trang
        
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Equipment> equipmentPage = equipmentRepository.findAll(pageable);
        
        // Thêm đối tượng equipment mới vào model cho form thêm mới
        if (!model.containsAttribute("equipment")) {
            model.addAttribute("equipment", new Equipment());
        }
        
        model.addAttribute("equipmentPage", equipmentPage);
        model.addAttribute("loaiThietBiList", loaiThietBiRepository.findAll());
        
        int totalPages = equipmentPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        
        return "admin/equipments";
    }
    
    @GetMapping("/equipments/add")
    public String showAddForm(Model model) {
        model.addAttribute("equipment", new Equipment());
        model.addAttribute("loaiThietBiList", loaiThietBiRepository.findAll());
        return "admin/equipments :: equipmentForm";
    }
    
    @PostMapping("/equipments/save")
    public String saveEquipment(@ModelAttribute("equipment") Equipment equipment, 
                              RedirectAttributes redirectAttributes) {
        equipmentRepository.save(equipment);
        redirectAttributes.addFlashAttribute("message", "Lưu thiết bị thành công!");
        return "redirect:/admin/equipments";
    }
    
    @GetMapping("/equipments/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID thiết bị không hợp lệ: " + id));
        model.addAttribute("equipment", equipment);
        model.addAttribute("loaiThietBiList", loaiThietBiRepository.findAll());
        return "admin/equipments :: equipmentForm";
    }
    
    @GetMapping("/equipments/delete/{id}")
    public String deleteEquipment(@PathVariable("id") Integer id, 
                                RedirectAttributes redirectAttributes) {
        try {
            equipmentRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa thiết bị thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa thiết bị. Có thể thiết bị đang được sử dụng.");
        }
        return "redirect:/admin/equipments";
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable("id") Integer id, Model model) {
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


    @GetMapping("/requests")
    public String requests(Model model) {
        List<YeuCauMuon> requests = yeuCauMuonRepository.findAllByTrangThai("Chờ phê duyệt");
        model.addAttribute("requests", requests);
        return "admin/requests";
    }

    @PostMapping("/requests/approve/{id}")
    public String approveRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        YeuCauMuon yeuCau = yeuCauMuonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
        yeuCau.setTrangThai("Đã duyệt");
        yeuCauMuonRepository.save(yeuCau);
        redirectAttributes.addFlashAttribute("message", "Yêu cầu đã được duyệt thành công!");
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/reject/{id}")
    public String rejectRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        YeuCauMuon yeuCau = yeuCauMuonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
        yeuCau.setTrangThai("Đã từ chối");
        yeuCauMuonRepository.save(yeuCau);
        redirectAttributes.addFlashAttribute("message", "Yêu cầu đã bị từ chối.");
        return "redirect:/admin/requests";
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

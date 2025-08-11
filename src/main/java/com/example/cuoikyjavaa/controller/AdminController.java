  package com.example.cuoikyjavaa.controller;

import com.example.cuoikyjavaa.dto.UserDTO;
import com.example.cuoikyjavaa.model.Equipment;
import com.example.cuoikyjavaa.model.YeuCauMuon;
import com.example.cuoikyjavaa.model.YeuCauThietBiMoi;
import com.example.cuoikyjavaa.repository.EquipmentRepository;
import com.example.cuoikyjavaa.repository.LoaiThietBiRepository;
import com.example.cuoikyjavaa.repository.UserRepository;
import com.example.cuoikyjavaa.repository.YeuCauMuonRepository;
import com.example.cuoikyjavaa.repository.YeuCauThietBiMoiRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.example.cuoikyjavaa.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    @Autowired
    private YeuCauMuonRepository yeuCauMuonRepository;

    @Autowired
    private YeuCauThietBiMoiRepository yeuCauThietBiMoiRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalEquipments = equipmentRepository.count();
        long pendingRequests = yeuCauMuonRepository.findAllByTrangThai("Chờ phê duyệt").size();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalEquipments", totalEquipments);
        model.addAttribute("pendingRequests", pendingRequests);

        List<Object[]> equipmentCountByType = equipmentRepository.countEquipmentByType();
        List<String> labels = equipmentCountByType.stream()
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
        List<Long> data = equipmentCountByType.stream()
                .map(result -> (Long) result[1])
                .collect(Collectors.toList());

        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartData", data);


        return "admin/dashboard";
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
                        u.getPhoneNumber(),
                        u.getRole()
                ))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/equipments/api/{id}")
    @ResponseBody
    public Equipment getEquipmentById(@PathVariable("id") Integer id) {
        return equipmentRepository.findById(id).orElse(null);
    }

    // ========== QUẢN LÝ THIẾT BỊ ==========
    
    @GetMapping("/equipments")
    public String equipments(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Equipment> equipmentPage = equipmentRepository.findAll(pageable);
        model.addAttribute("equipmentPage", equipmentPage);
        model.addAttribute("loaiThietBiList", loaiThietBiRepository.findAll());
        model.addAttribute("equipment", new Equipment());

        int totalPages = equipmentPage.getTotalPages();
        if (totalPages > 0) {
            int start = Math.max(1, page - 2);
            int end = Math.min(page + 3, totalPages);

            if (page > 3) {
                List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            } else {
                 List<Integer> pageNumbers = IntStream.rangeClosed(1, Math.min(5, totalPages))
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

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
    
    @PostMapping("/equipments/delete/{id}")
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
                user.getPhoneNumber(),
                user.getRole()
        );
        model.addAttribute("user", dto);
        return "admin/user_edit";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @ModelAttribute("user") UserDTO userDetails, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Cập nhật người dùng thành công!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        if (id != null && userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
        return "redirect:/admin/users";
    }


    @GetMapping("/requests")
    public String requests(Model model) {
        List<YeuCauMuon> borrowRequests = yeuCauMuonRepository.findAllByTrangThai("Chờ phê duyệt");
        model.addAttribute("borrowRequests", borrowRequests);

        List<YeuCauThietBiMoi> newEquipmentRequests = yeuCauThietBiMoiRepository.findAll();
        model.addAttribute("newEquipmentRequests", newEquipmentRequests);

        return "admin/requests";
    }

    @PostMapping("/requests/approve/{id}")
    public String approveRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        YeuCauMuon yeuCau = yeuCauMuonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        Equipment equipment = yeuCau.getThietBi();
        if (equipment != null) {
            if ("Sẵn sàng".equals(equipment.getTrangThai())) {
                equipment.setTrangThai("Đang sử dụng");
                equipmentRepository.save(equipment);

                yeuCau.setTrangThai("Đã duyệt");
                yeuCauMuonRepository.save(yeuCau);
                redirectAttributes.addFlashAttribute("message", "Đã duyệt yêu cầu mượn. Trạng thái thiết bị đã được cập nhật thành 'Đang sử dụng'.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không thể duyệt yêu cầu. Thiết bị '" + equipment.getTenThietBi() + "' hiện không có sẵn.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Yêu cầu không hợp lệ do không liên kết với thiết bị nào.");
        }
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/reject/{id}")
    public String rejectRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        YeuCauMuon yeuCau = yeuCauMuonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
        yeuCau.setTrangThai("Đã từ chối");
        yeuCauMuonRepository.save(yeuCau);
        redirectAttributes.addFlashAttribute("message", "Đã từ chối yêu cầu mượn.");
        return "redirect:/admin/requests";
    }

    // --- New Equipment Request Actions ---

    @PostMapping("/requests/new/reject/{id}")
    public String rejectNewEquipmentRequest(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        YeuCauThietBiMoi yeuCau = yeuCauThietBiMoiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid new equipment request Id:" + id));
        yeuCau.setStatus("Đã từ chối");
        yeuCauThietBiMoiRepository.save(yeuCau);
        redirectAttributes.addFlashAttribute("message", "Đã từ chối yêu cầu cấp mới thiết bị.");
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/new/confirm")
    public String confirmNewEquipmentRequest(@RequestParam("requestId") Integer requestId,
                                             @RequestParam("tenThietBiPrefix") String tenThietBiPrefix,
                                             @RequestParam("maThietBiPrefix") String maThietBiPrefix,
                                             @RequestParam("viTri") String viTri,
                                             RedirectAttributes redirectAttributes) {
        YeuCauThietBiMoi yeuCau = yeuCauThietBiMoiRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid new equipment request Id:" + requestId));

        try {
            for (int i = 1; i <= yeuCau.getQuantity(); i++) {
                Equipment newEquipment = new Equipment();
                // Logic to generate unique maThietBi
                String maThietBi = maThietBiPrefix + "-" + System.currentTimeMillis() + "-" + i;
                while (equipmentRepository.existsByMaThietBi(maThietBi)) {
                    maThietBi = maThietBiPrefix + "-" + System.currentTimeMillis() + "-" + (i+1); // Ensure uniqueness
                }

                newEquipment.setMaThietBi(maThietBi);
                newEquipment.setTenThietBi(tenThietBiPrefix + " " + i);
                newEquipment.setLoaiThietBi(yeuCau.getLoaiThietBi());
                newEquipment.setTrangThai("Sẵn sàng");
                newEquipment.setViTri(viTri);
                newEquipment.setNgayNhap(LocalDate.now());
                equipmentRepository.save(newEquipment);
            }

            yeuCau.setStatus("Đã thêm");
            yeuCauThietBiMoiRepository.save(yeuCau);
            redirectAttributes.addFlashAttribute("message", "Đã thêm " + yeuCau.getQuantity() + " thiết bị mới thành công.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm thiết bị mới: " + e.getMessage());
        }

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



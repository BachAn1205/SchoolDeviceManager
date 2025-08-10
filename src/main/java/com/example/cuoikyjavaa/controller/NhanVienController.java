package com.example.cuoikyjavaa.controller;

import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.model.YeuCauThietBiMoi;
import com.example.cuoikyjavaa.repository.LoaiThietBiRepository;
import com.example.cuoikyjavaa.repository.UserRepository;
import com.example.cuoikyjavaa.repository.YeuCauThietBiMoiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import com.example.cuoikyjavaa.model.BaoCaoSuCo;
import com.example.cuoikyjavaa.repository.BaoCaoSuCoRepository;
import com.example.cuoikyjavaa.repository.EquipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/nhanvien")
public class NhanVienController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private YeuCauThietBiMoiRepository yeuCauThietBiMoiRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private BaoCaoSuCoRepository baoCaoSuCoRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login.html";
        }
        model.addAttribute("user", user);
        return "nhanvien/dashboard";
    }

    @GetMapping("/request_equipment")
    public String requestEquipment(Model model) {
        model.addAttribute("newRequest", new YeuCauThietBiMoi());
        model.addAttribute("loaiThietBiList", loaiThietBiRepository.findAll());
        return "nhanvien/request_equipment";
    }

    @PostMapping("/request_equipment")
    public String submitRequestEquipment(@ModelAttribute("newRequest") YeuCauThietBiMoi newRequest,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         RedirectAttributes redirectAttributes) {
        User requester = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (requester == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng. Vui lòng đăng nhập lại.");
            return "redirect:/login.html";
        }

        newRequest.setRequester(requester);
        newRequest.setRequestDate(LocalDateTime.now());
        newRequest.setStatus("Đang chờ"); // Initial status

        yeuCauThietBiMoiRepository.save(newRequest);

        redirectAttributes.addFlashAttribute("message", "Yêu cầu của bạn đã được gửi thành công!");
        return "redirect:/nhanvien/request_equipment";
    }

    @GetMapping("/my_requests")
    public String myRequests(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xem yêu cầu.");
            return "redirect:/login.html";
        }
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng."));

        List<YeuCauThietBiMoi> newEquipmentRequests = yeuCauThietBiMoiRepository.findByRequester(currentUser);
        model.addAttribute("newEquipmentRequests", newEquipmentRequests);

        List<BaoCaoSuCo> issueReports = baoCaoSuCoRepository.findByNguoiBaoCao(currentUser);
        model.addAttribute("issueReports", issueReports);

        return "nhanvien/my_requests";
    }

    @PostMapping("/my_requests/cancel/{id}")
    public String cancelRequest(@PathVariable("id") Integer id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login.html";
        }

        YeuCauThietBiMoi request = yeuCauThietBiMoiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu với id: " + id));

        // Ensure the user can only cancel their own requests
        if (!request.getRequester().getUsername().equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền hủy yêu cầu này.");
            return "redirect:/nhanvien/my_requests";
        }

        if ("Đang chờ".equals(request.getStatus())) {
            yeuCauThietBiMoiRepository.delete(request);
            redirectAttributes.addFlashAttribute("message", "Đã hủy yêu cầu thành công.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể hủy các yêu cầu đang ở trạng thái 'Đang chờ'.");
        }

        return "redirect:/nhanvien/my_requests";
    }

    @GetMapping("/report_issue")
    public String showReportIssueForm(Model model,
                                      @RequestParam(value = "loaiThietBiId", required = false) Integer loaiThietBiId,
                                      @RequestParam(value = "keyword", required = false) String keyword) {
        model.addAttribute("newBaoCao", new BaoCaoSuCo());
        model.addAttribute("loaiThietBiList", loaiThietBiRepository.findAll());

        // Handle empty keyword
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        model.addAttribute("equipments", equipmentRepository.searchEquipments(searchKeyword, loaiThietBiId, null));
        
        // Pass back search params to keep them in the form
        model.addAttribute("selectedLoaiId", loaiThietBiId);
        model.addAttribute("keyword", keyword);
        
        return "nhanvien/report_issue";
    }

    @PostMapping("/report_issue")
    public String submitReportIssue(@ModelAttribute("newBaoCao") BaoCaoSuCo baoCaoSuCo,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login.html";
        }
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng."));

        baoCaoSuCo.setNguoiBaoCao(currentUser);
        baoCaoSuCo.setNgayBaoCao(LocalDateTime.now());
        baoCaoSuCo.setTrangThai("Cần xử lý");

        baoCaoSuCoRepository.save(baoCaoSuCo);

        redirectAttributes.addFlashAttribute("message", "Báo cáo sự cố đã được gửi thành công.");
        return "redirect:/nhanvien/report_issue";
    }

    @GetMapping("/equipments")
    public String viewEquipments(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.example.cuoikyjavaa.model.Equipment> equipmentPage = equipmentRepository.findAll(pageable);
        model.addAttribute("equipmentPage", equipmentPage);

        int totalPages = equipmentPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "nhanvien/equipments";
    }

    @GetMapping("/staff_profile")
    public String staffProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng."));
        model.addAttribute("user", currentUser);
        return "nhanvien/staff_profile";
    }

    @PostMapping("/staff_profile/update")
    public String updateStaffProfile(@ModelAttribute("user") User updatedUser, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng."));

        currentUser.setFullName(updatedUser.getFullName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setPhoneNumber(updatedUser.getPhoneNumber());

        userRepository.save(currentUser);

        redirectAttributes.addFlashAttribute("message", "Cập nhật hồ sơ thành công!");
        return "redirect:/nhanvien/staff_profile";
    }
}

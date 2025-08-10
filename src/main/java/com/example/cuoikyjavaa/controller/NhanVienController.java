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

@Controller
@RequestMapping("/nhanvien")
public class NhanVienController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private YeuCauThietBiMoiRepository yeuCauThietBiMoiRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

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

        List<YeuCauThietBiMoi> requests = yeuCauThietBiMoiRepository.findByRequester(currentUser);
        model.addAttribute("requests", requests);

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
    public String reportIssue() {
        return "nhanvien/report_issue";
    }

    @GetMapping("/staff_profile")
    public String staffProfile() {
        return "nhanvien/staff_profile";
    }
}

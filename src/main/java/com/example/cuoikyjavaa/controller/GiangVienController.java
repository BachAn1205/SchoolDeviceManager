package com.example.cuoikyjavaa.controller;

import com.example.cuoikyjavaa.model.Equipment;
import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.model.YeuCauMuon;
import com.example.cuoikyjavaa.repository.EquipmentRepository;
import com.example.cuoikyjavaa.repository.LoaiThietBiRepository;
import com.example.cuoikyjavaa.repository.UserRepository;
import com.example.cuoikyjavaa.repository.YeuCauMuonRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/giangvien")
public class GiangVienController {
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    @Autowired
    private YeuCauMuonRepository yeuCauMuonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login.html";
        }

        List<YeuCauMuon> userRequests = yeuCauMuonRepository.findByNguoiGui(user);

        // Data for cards
        long pendingRequests = userRequests.stream().filter(r -> "Chờ phê duyệt".equals(r.getTrangThai())).count();
        long approvedRequests = userRequests.stream().filter(r -> "Đã duyệt".equals(r.getTrangThai())).count();
        long rejectedRequests = userRequests.stream().filter(r -> "Đã từ chối".equals(r.getTrangThai())).count();
        int borrowedDevices = userRequests.stream()
            .filter(r -> "Đã duyệt".equals(r.getTrangThai()))
            .mapToInt(YeuCauMuon::getSoLuong)
            .sum();

        // Data for borrow trend chart (last 6 months)
        Map<String, Integer> monthlyTrend = userRequests.stream()
            .filter(r -> "Đã duyệt".equals(r.getTrangThai()) && r.getNgayMuon() != null)
            .collect(Collectors.groupingBy(
                r -> r.getNgayMuon().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.summingInt(YeuCauMuon::getSoLuong)
            ));

        List<String> trendLabels = new ArrayList<>();
        List<Integer> trendData = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            trendLabels.add("Tháng " + month.getMonthValue());
            trendData.add(monthlyTrend.getOrDefault(monthKey, 0));
        }


        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("approvedRequests", approvedRequests);
        model.addAttribute("borrowedDevices", borrowedDevices);
        model.addAttribute("rejectedRequests", rejectedRequests);
        model.addAttribute("user", user);
        model.addAttribute("trendLabels", trendLabels);
        model.addAttribute("trendData", trendData);

        return "giangvien/dashboard";
    }

    @GetMapping("/search_equipment")
    public String searchEquipment(Model model,
                                  @RequestParam(value = "tenThietBi", required = false) String tenThietBi,
                                  @RequestParam(value = "loaiThietBiId", required = false) Integer loaiThietBiId,
                                  @RequestParam(value = "trangThai", required = false) String trangThai) {

        // Xử lý giá trị đầu vào: nếu chuỗi rỗng thì coi là null để query đúng
        String searchName = (tenThietBi != null && !tenThietBi.trim().isEmpty()) ? tenThietBi.trim() : null;
        String searchStatus = (trangThai != null && !trangThai.isEmpty()) ? trangThai : null;

        List<Equipment> equipmentList = equipmentRepository.searchEquipments(searchName, loaiThietBiId, searchStatus);
        model.addAttribute("equipments", equipmentList);
        model.addAttribute("loaiThietBiList", loaiThietBiRepository.findAll());
        model.addAttribute("trangThaiList", Arrays.asList("Sẵn sàng", "Đang mượn", "Bảo trì"));

        // Giữ lại giá trị tìm kiếm trên form
        model.addAttribute("tenThietBi", tenThietBi);
        model.addAttribute("loaiThietBiId", loaiThietBiId);
        model.addAttribute("trangThai", trangThai);

        return "giangvien/search_equipment";
    }

    @GetMapping("/submit_borrow_request")
    public String showBorrowRequestForm(Model model, @RequestParam(value = "thietBiId", required = false) Integer thietBiId) {
        YeuCauMuon yeuCauMuon = new YeuCauMuon();
        if (thietBiId != null) {
            equipmentRepository.findById(thietBiId).ifPresent(yeuCauMuon::setThietBi);
        }
        model.addAttribute("yeuCauMuon", yeuCauMuon);
        model.addAttribute("equipments", equipmentRepository.findAll());
        return "giangvien/submit_borrow_request";
    }

    @PostMapping("/submit_borrow_request")
    public String submitBorrowRequest(@ModelAttribute("yeuCauMuon") YeuCauMuon yeuCauMuon,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng.");
            return "redirect:/giangvien/submit_borrow_request";
        }

        yeuCauMuon.setNguoiGui(user);
        yeuCauMuon.setNgayGui(LocalDateTime.now());
        yeuCauMuon.setTrangThai("Chờ phê duyệt");
        yeuCauMuonRepository.save(yeuCauMuon);

        redirectAttributes.addFlashAttribute("message", "Gửi yêu cầu mượn thành công!");
        return "redirect:/giangvien/my_requests";
    }

    @PostMapping("/my_requests/cancel/{id}")
    public String cancelRequest(@PathVariable("id") Integer id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login.html";
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        YeuCauMuon yeuCau = yeuCauMuonRepository.findById(id).orElse(null);

        if (yeuCau == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy yêu cầu.");
            return "redirect:/giangvien/my_requests";
        }

        if (currentUser == null || !yeuCau.getNguoiGui().getUserID().equals(currentUser.getUserID())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền hủy yêu cầu này.");
            return "redirect:/giangvien/my_requests";
        }

        if (!"Chờ phê duyệt".equals(yeuCau.getTrangThai())) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể hủy các yêu cầu đang chờ phê duyệt.");
            return "redirect:/giangvien/my_requests";
        }

        yeuCauMuonRepository.delete(yeuCau);
        redirectAttributes.addFlashAttribute("message", "Đã hủy yêu cầu thành công.");
        return "redirect:/giangvien/my_requests";
    }

    @PostMapping("/borrow_history/delete/{id}")
    public String deleteRejectedRequest(@PathVariable("id") Integer id,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login.html";
        
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        YeuCauMuon yeuCau = yeuCauMuonRepository.findById(id).orElse(null);

        if (yeuCau == null || currentUser == null || !yeuCau.getNguoiGui().getUserID().equals(currentUser.getUserID())) {
            redirectAttributes.addFlashAttribute("error", "Hành động không được phép.");
            return "redirect:/giangvien/borrow_history";
        }

        if (!"Đã từ chối".equals(yeuCau.getTrangThai())) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể xóa các yêu cầu đã bị từ chối.");
            return "redirect:/giangvien/borrow_history";
        }

        yeuCauMuonRepository.delete(yeuCau);
        redirectAttributes.addFlashAttribute("message", "Đã xóa yêu cầu thành công.");
        return "redirect:/giangvien/borrow_history";
    }

    @PostMapping("/borrow_history/return/{id}")
    public String returnDevice(@PathVariable("id") Integer id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login.html";

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        YeuCauMuon yeuCau = yeuCauMuonRepository.findById(id).orElse(null);

        if (yeuCau == null || currentUser == null || !yeuCau.getNguoiGui().getUserID().equals(currentUser.getUserID())) {
            redirectAttributes.addFlashAttribute("error", "Hành động không được phép.");
            return "redirect:/giangvien/borrow_history";
        }

        if (!"Đã duyệt".equals(yeuCau.getTrangThai())) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể trả các thiết bị có yêu cầu đã được duyệt.");
            return "redirect:/giangvien/borrow_history";
        }

        yeuCau.setTrangThai("Đã trả");
        yeuCauMuonRepository.save(yeuCau);
        redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái trả thiết bị thành công.");
        return "redirect:/giangvien/borrow_history";
    }

    @GetMapping("/my_requests")
    public String myRequests(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xem yêu cầu của bạn.");
            return "redirect:/login.html";
        }
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            model.addAttribute("requests", yeuCauMuonRepository.findByNguoiGuiAndTrangThai(user, "Chờ phê duyệt"));
        }
        // Chuẩn bị đối tượng và danh sách cho modal
        model.addAttribute("newYeuCauMuon", new YeuCauMuon());
        model.addAttribute("equipments", equipmentRepository.findAll());
        return "giangvien/my_requests";
    }

    @GetMapping("/borrow_history")
    public String borrowHistory(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xem lịch sử.");
            return "redirect:/login.html";
        }
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            model.addAttribute("requests", yeuCauMuonRepository.findByNguoiGui(user));
        }
        return "giangvien/borrow_history";
    }

    @GetMapping("/lecturer_profile")
    public String lecturerProfile(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xem hồ sơ.");
            return "redirect:/login.html";
        }
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        model.addAttribute("user", user);
        return "giangvien/lecturer_profile";
    }

    @PostMapping("/lecturer_profile/update")
    public String updateLecturerProfile(@ModelAttribute("user") User userForm,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login.html";
        }

        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng để cập nhật.");
            return "redirect:/giangvien/lecturer_profile";
        }

        // Cập nhật các trường được phép thay đổi
        currentUser.setFullName(userForm.getFullName());
        currentUser.setEmail(userForm.getEmail());
        currentUser.setPhone(userForm.getPhone());

        userRepository.save(currentUser);
        redirectAttributes.addFlashAttribute("message", "Cập nhật hồ sơ thành công!");
        return "redirect:/giangvien/lecturer_profile";
    }
}

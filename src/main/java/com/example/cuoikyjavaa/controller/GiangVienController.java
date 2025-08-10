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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/giangvien")
public class GiangVienController {
    private final EquipmentRepository equipmentRepository;
    private final LoaiThietBiRepository loaiThietBiRepository;
    private final YeuCauMuonRepository yeuCauMuonRepository;
    private final UserRepository userRepository;


    public GiangVienController(EquipmentRepository equipmentRepository, LoaiThietBiRepository loaiThietBiRepository, YeuCauMuonRepository yeuCauMuonRepository, UserRepository userRepository) {
        this.equipmentRepository = equipmentRepository;
        this.loaiThietBiRepository = loaiThietBiRepository;
        this.yeuCauMuonRepository = yeuCauMuonRepository;
        this.userRepository = userRepository;
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

    @PostMapping("/create_borrow_request")
    public String createBorrowRequest(@RequestParam("thietBiId") Integer thietBiId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.");
            return "redirect:/giangvien/search_equipment";
        }

        Equipment equipment = equipmentRepository.findById(thietBiId).orElse(null);
        if (equipment == null) {
            redirectAttributes.addFlashAttribute("error", "Thiết bị không tồn tại.");
            return "redirect:/giangvien/search_equipment";
        }

        YeuCauMuon yeuCauMuon = new YeuCauMuon();
        yeuCauMuon.setNguoiGui(user);
        yeuCauMuon.setThietBi(equipment);
        yeuCauMuon.setSoLuong(1); // Mặc định số lượng là 1
        yeuCauMuon.setNgayGui(LocalDateTime.now());
        yeuCauMuon.setTrangThai("Chờ phê duyệt");

        yeuCauMuonRepository.save(yeuCauMuon);

        redirectAttributes.addFlashAttribute("message", "Gửi yêu cầu mượn cho thiết bị '" + equipment.getTenThietBi() + "' thành công!");
        return "redirect:/giangvien/search_equipment";
    }

    @PostMapping("/my_requests/add")
    public String addBorrowRequest(@ModelAttribute("newYeuCauMuon") YeuCauMuon yeuCauMuon,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng.");
            return "redirect:/giangvien/my_requests";
        }

        yeuCauMuon.setNguoiGui(user);
        yeuCauMuon.setNgayGui(LocalDateTime.now());
        yeuCauMuon.setTrangThai("Chờ phê duyệt");
        yeuCauMuonRepository.save(yeuCauMuon);

        redirectAttributes.addFlashAttribute("message", "Tạo yêu cầu mượn thành công!");
        return "redirect:/giangvien/my_requests";
    }

    @GetMapping("/my_requests")
    public String myRequests(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xem yêu cầu của bạn.");
            return "redirect:/login.html";
        }
        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user != null) {
            model.addAttribute("requests", yeuCauMuonRepository.findByNguoiGui(user));
        }
        // Chuẩn bị đối tượng và danh sách cho modal
        model.addAttribute("newYeuCauMuon", new YeuCauMuon());
        model.addAttribute("equipments", equipmentRepository.findAll());
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

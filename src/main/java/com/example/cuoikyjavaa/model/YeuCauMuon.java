package com.example.cuoikyjavaa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_cau_muon")
public class YeuCauMuon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nguoi_gui_id", nullable = false)
    private User nguoiGui;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "thiet_bi_id", nullable = false)
    private Equipment thietBi;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "ngay_muon")
    private LocalDateTime ngayMuon;

    @Column(name = "ngay_tra_du_kien")
    private LocalDateTime ngayTraDuKien;

    @Column(name = "ngay_gui", nullable = true)
    private LocalDateTime ngayGui;

    @Column(name = "trang_thai")
    private String trangThai;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getNguoiGui() { return nguoiGui; }
    public void setNguoiGui(User nguoiGui) { this.nguoiGui = nguoiGui; }

    public Equipment getThietBi() { return thietBi; }
    public void setThietBi(Equipment thietBi) { this.thietBi = thietBi; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public LocalDateTime getNgayMuon() { return ngayMuon; }
    public void setNgayMuon(LocalDateTime ngayMuon) { this.ngayMuon = ngayMuon; }

    public LocalDateTime getNgayTraDuKien() { return ngayTraDuKien; }
    public void setNgayTraDuKien(LocalDateTime ngayTraDuKien) { this.ngayTraDuKien = ngayTraDuKien; }

    public LocalDateTime getNgayGui() { return ngayGui; }
    public void setNgayGui(LocalDateTime ngayGui) { this.ngayGui = ngayGui; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}

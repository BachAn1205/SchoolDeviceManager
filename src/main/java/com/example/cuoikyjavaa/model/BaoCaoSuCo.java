package com.example.cuoikyjavaa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bao_cao_su_co")
public class BaoCaoSuCo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "nguoi_bao_cao_id", nullable = false)
    private User nguoiBaoCao;

    @ManyToOne
    @JoinColumn(name = "thiet_bi_id", nullable = false)
    private Equipment thietBi;

    @Column(name = "ngay_bao_cao", nullable = false)
    private LocalDateTime ngayBaoCao;

    @Column(name = "mo_ta_su_co")
    private String moTaSuCo;

    @Column(name = "trang_thai")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "ky_thuat_vien_id")
    private User kyThuatVien;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getNguoiBaoCao() {
        return nguoiBaoCao;
    }

    public void setNguoiBaoCao(User nguoiBaoCao) {
        this.nguoiBaoCao = nguoiBaoCao;
    }

    public Equipment getThietBi() {
        return thietBi;
    }

    public void setThietBi(Equipment thietBi) {
        this.thietBi = thietBi;
    }

    public LocalDateTime getNgayBaoCao() {
        return ngayBaoCao;
    }

    public void setNgayBaoCao(LocalDateTime ngayBaoCao) {
        this.ngayBaoCao = ngayBaoCao;
    }

    public String getMoTaSuCo() {
        return moTaSuCo;
    }

    public void setMoTaSuCo(String moTaSuCo) {
        this.moTaSuCo = moTaSuCo;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public User getKyThuatVien() {
        return kyThuatVien;
    }

    public void setKyThuatVien(User kyThuatVien) {
        this.kyThuatVien = kyThuatVien;
    }
}
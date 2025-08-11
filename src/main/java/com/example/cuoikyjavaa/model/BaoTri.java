package com.example.cuoikyjavaa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bao_tri")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaoTri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "thiet_bi_id", nullable = false)
    private Equipment thietBi;

    @Column(name = "ngay_bao_tri")
    private LocalDateTime ngayBaoTri;

    @Column(name = "noi_dung", length = 255)
    private String noiDung;

    @ManyToOne
    @JoinColumn(name = "ky_thuat_vien_id")
    private User kyThuatVien;

    @Column(name = "trang_thai", length = 255)
    private String trangThai;
}
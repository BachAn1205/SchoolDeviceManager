package com.example.cuoikyjavaa.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "thiet_bi")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_thiet_bi", length = 255)
    private String maThietBi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loai_thiet_bi_id", nullable = false)
    private LoaiThietBi loaiThietBi;

    @Column(name = "trang_thai", length = 255)
    private String trangThai;

    @Column(name = "vi_tri", length = 255)
    private String viTri;

    @Column(name = "ngay_nhap")
    private LocalDate ngayNhap;

    @Column(name = "ten_thiet_bi", length = 255)
    private String tenThietBi;

    public Equipment() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMaThietBi() { return maThietBi; }
    public void setMaThietBi(String maThietBi) { this.maThietBi = maThietBi; }

    public LoaiThietBi getLoaiThietBi() { return loaiThietBi; }
    public void setLoaiThietBi(LoaiThietBi loaiThietBi) { this.loaiThietBi = loaiThietBi; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getViTri() { return viTri; }
    public void setViTri(String viTri) { this.viTri = viTri; }

    public LocalDate getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }

    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }
}

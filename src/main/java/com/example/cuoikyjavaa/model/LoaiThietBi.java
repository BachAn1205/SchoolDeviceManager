package com.example.cuoikyjavaa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "loai_thiet_bi")
public class LoaiThietBi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_loai", length = 255)
    private String tenLoai;

    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}

package com.example.cuoikyjavaa.repository;

import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.model.YeuCauMuon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YeuCauMuonRepository extends JpaRepository<YeuCauMuon, Integer> {
    List<YeuCauMuon> findAllByTrangThai(String trangThai);
    List<YeuCauMuon> findByNguoiGui(User user);
}

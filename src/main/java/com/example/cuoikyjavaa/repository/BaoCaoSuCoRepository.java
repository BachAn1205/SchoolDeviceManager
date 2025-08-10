package com.example.cuoikyjavaa.repository;

import com.example.cuoikyjavaa.model.BaoCaoSuCo;
import com.example.cuoikyjavaa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaoCaoSuCoRepository extends JpaRepository<BaoCaoSuCo, Integer> {
    List<BaoCaoSuCo> findByNguoiBaoCao(User nguoiBaoCao);
}
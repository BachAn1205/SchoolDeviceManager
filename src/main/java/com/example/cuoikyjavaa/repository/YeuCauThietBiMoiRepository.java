package com.example.cuoikyjavaa.repository;

import com.example.cuoikyjavaa.model.User;
import com.example.cuoikyjavaa.model.YeuCauThietBiMoi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YeuCauThietBiMoiRepository extends JpaRepository<YeuCauThietBiMoi, Integer> {
    List<YeuCauThietBiMoi> findByRequester(User requester);
}
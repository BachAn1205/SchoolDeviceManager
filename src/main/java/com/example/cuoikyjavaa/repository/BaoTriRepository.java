package com.example.cuoikyjavaa.repository;

import com.example.cuoikyjavaa.model.BaoTri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaoTriRepository extends JpaRepository<BaoTri, Integer> {
}
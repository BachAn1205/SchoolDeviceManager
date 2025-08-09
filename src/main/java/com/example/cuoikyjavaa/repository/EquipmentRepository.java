package com.example.cuoikyjavaa.repository;

import com.example.cuoikyjavaa.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    boolean existsByMaThietBi(String maThietBi);
    Optional<Equipment> findByMaThietBi(String maThietBi);
}

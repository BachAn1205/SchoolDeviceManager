package com.example.cuoikyjavaa.repository;

import com.example.cuoikyjavaa.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    boolean existsByMaThietBi(String maThietBi);
    Optional<Equipment> findByMaThietBi(String maThietBi);

    @Query("SELECT e FROM Equipment e WHERE " +
            "(:tenThietBi IS NULL OR e.tenThietBi LIKE %:tenThietBi%) AND " +
            "(:loaiThietBiId IS NULL OR e.loaiThietBi.id = :loaiThietBiId) AND " +
            "(:trangThai IS NULL OR e.trangThai = :trangThai)")
    List<Equipment> searchEquipments(@Param("tenThietBi") String tenThietBi,
                                     @Param("loaiThietBiId") Integer loaiThietBiId,
                                     @Param("trangThai") String trangThai);

    @Query("SELECT l.tenLoai, COUNT(e) FROM Equipment e JOIN e.loaiThietBi l GROUP BY l.tenLoai")
    List<Object[]> countEquipmentByType();
}

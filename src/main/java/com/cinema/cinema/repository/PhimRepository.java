package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cinema.cinema.entity.Phim;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface    PhimRepository extends JpaRepository<Phim, Integer> {
    // 1. LẤY PHIM SẮP CHIẾU
    @Query("SELECT p FROM Phim p WHERE p.ngayPhatHanh > :homNay ORDER BY p.ngayPhatHanh ASC")
    List<Phim> findPhimSapChieu(@Param("homNay") LocalDate homNay);

    // 2. LẤY PHIM ĐANG CHIẾU
    @Query("SELECT DISTINCT p FROM Phim p JOIN SuatChieu s ON p.id = s.phim.id " +
            "WHERE s.thoiGianBatDau >= :thoiDiemHienTai ORDER BY p.ngayPhatHanh DESC")
    List<Phim> findPhimDangChieu(@Param("thoiDiemHienTai") LocalDateTime thoiDiemHienTai);
}

package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.cinema.entity.SuatChieu;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuatChieuRepository extends JpaRepository<SuatChieu, Integer>,
                JpaSpecificationExecutor<SuatChieu> {

        // Tìm các suất chiếu của một phòng trong 1 khoảng thời gian
        @Query("SELECT s FROM SuatChieu s WHERE s.phongChieu.id = :idPhong " +
                        "AND ((s.thoiGianBatDau <= :ketThuc) AND (s.thoiGianBatDau >= :batDau))")
        List<SuatChieu> findOverlappingShowtimes(Integer idPhong, LocalDateTime batDau, LocalDateTime ketThuc);

        // Danh sách suất chiếu
        List<SuatChieu> findByPhim_IdPhimAndThoiGianBatDauAfter(Integer phimId, LocalDateTime now);

        // Khách hàng lấy suất chiếu theo phim
        @Query("SELECT s FROM SuatChieu s WHERE s.phim.idPhim = :phimId " +
                        "AND s.thoiGianBatDau >= :startOfDay " +
                        "AND s.thoiGianBatDau <= :endOfDay " +
                        "AND s.thoiGianBatDau > :thoiGianHienTai ORDER BY s.thoiGianBatDau")
        List<SuatChieu> findByPhimAndNgay(
                        @Param("phimId") Integer phimId,
                        @Param("startOfDay") LocalDateTime startOfDay,
                        @Param("endOfDay") LocalDateTime endOfDay,
                        @Param("thoiGianHienTai") LocalDateTime thoiGianHienTai);

        // Khách hàng lấy suất chiếu theo rạp
        @Query("SELECT s FROM SuatChieu s WHERE s.phongChieu.rap.idRap = :rapId " +
                        "AND s.thoiGianBatDau >= :startOfDay " +
                        "AND s.thoiGianBatDau <= :endOfDay " +
                        "AND s.thoiGianBatDau > :thoiGianHienTai ORDER BY s.thoiGianBatDau")
        List<SuatChieu> findByRapAndNgay(
                        @Param("rapId") Integer rapId,
                        @Param("startOfDay") LocalDateTime startOfDay,
                        @Param("endOfDay") LocalDateTime endOfDay,
                        @Param("thoiGianHienTai") LocalDateTime thoiGianHienTai);
}

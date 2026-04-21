package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.cinema.entity.SuatChieu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuatChieuRepository extends JpaRepository<SuatChieu, Integer> {

    //Tìm các suất chiếu của một phòng trong 1 khoảng thời gian
    @Query("SELECT s FROM SuatChieu s WHERE s.phongChieu.id = :idPhong " +
            "AND ((s.thoiGianBatDau <= :ketThuc) AND (s.thoiGianBatDau >= :batDau))")
    List<SuatChieu> findOverlappingShowtimes(Integer idPhong, LocalDateTime batDau, LocalDateTime ketThuc);

    //Danh sách suất chiếu
    List<SuatChieu> findByPhimIdAndThoiGianBatDauAfter(Integer phimId, LocalDateTime now);

    //Khách hàng lấy suất chiếu theo phim
    @Query("SELECT s FROM SuatChieu s WHERE s.phim.id = :phimId " +
            "AND FUNCTION('DATE', s.thoiGianBatDau) = :ngay " +
            "AND s.thoiGianBatDau > :thoiGianHienTai ORDER BY s.thoiGianBatDau")
    List<SuatChieu> findByPhimAndNgay(Integer phimId, LocalDate ngay, LocalDateTime thoiGianHienTai);

    //Khách hàng lấy suất chiếu theo rạp
    @Query("SELECT s FROM SuatChieu s WHERE s.phongChieu.rap.id = :rapId " +
            "AND FUNCTION('DATE', s.thoiGianBatDau) = :ngay " +
            "AND s.thoiGianBatDau > :thoiGianHienTai ORDER BY s.thoiGianBatDau")
    List<SuatChieu> findByRapAndNgay(Integer rapId, LocalDate ngay, LocalDateTime thoiGianHienTai);
}

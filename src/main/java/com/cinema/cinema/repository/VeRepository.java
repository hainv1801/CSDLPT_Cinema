package com.cinema.cinema.repository;

import com.cinema.cinema.dto.response.DoanhThuNgayDTO;
import com.cinema.cinema.dto.response.DoanhThuPhimDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.cinema.entity.Ve;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VeRepository extends JpaRepository<Ve, Integer> {

    //1. Thống kê doanh thu theo phim
    @Query("SELECT p.id AS idPhim, p.ten AS tenPhim, COUNT(v.id) AS soVeDaBan, SUM(s.giaMoiVe) AS tongDoanhThu " +
            "FROM Ve v JOIN v.hoaDon h JOIN v.suatChieu s JOIN s.phim p " +
            "WHERE h.trangThai = 'DATHANHTOAN' " +
            "AND h.ngayThanhToan >= :tuNgay AND h.ngayThanhToan <= :denNgay " +
            "GROUP BY p.id, p.ten " +
            "ORDER BY tongDoanhThu DESC")
    List<DoanhThuPhimDTO> thongKeDoanhThuTheoPhim(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay);

    //2. Thống kê doanh thu theo ngày
    @Query("SELECT FUNCTION('DATE', h.ngayThanhToan) AS ngay, COUNT(v.id) AS soVeDaBan, SUM(s.giaMoiVe) AS tongDoanhThu " +
            "FROM Ve v JOIN v.hoaDon h JOIN v.suatChieu s " +
            "WHERE h.trangThai = 'DATHANHTOAN' " +
            "AND h.ngayThanhToan >= :tuNgay AND h.ngayThanhToan <= :denNgay " +
            "GROUP BY FUNCTION('DATE', h.ngayThanhToan) " +
            "ORDER BY ngay ASC")
    List<DoanhThuNgayDTO> thongKeDoanhThuTheoNgay(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay);
}

package com.cinema.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.cinema.cinema.dto.response.HoaDonGiaoDichDTO;
import com.cinema.cinema.entity.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer>, JpaSpecificationExecutor<HoaDon> {
    List<HoaDon> findByNguoiDung_IdNguoiDungOrderByNgayThanhToanDesc(Integer idNguoiDung);

    @Query(value = "SELECT SUM(sc.giaMoiVe) FROM HoaDon h " +
            "JOIN Ve v ON h.id_HoaDon = v.id_HoaDon " +
            "JOIN SuatChieu sc ON v.id_SuatChieu = sc.id_SuatChieu " +
            "WHERE h.trangThai = 'DATHANHTOAN'", nativeQuery = true)
    Integer sumTotalRevenue();

    // Nhóm doanh thu theo từng chi nhánh (maCoSo)
    @Query(value = "SELECT h.maCoSo, SUM(sc.giaMoiVe) FROM HoaDon h " +
            "JOIN Ve v ON h.id_HoaDon = v.id_HoaDon " +
            "JOIN SuatChieu sc ON v.id_SuatChieu = sc.id_SuatChieu " +
            "WHERE h.trangThai = 'DATHANHTOAN' " +
            "GROUP BY h.maCoSo", nativeQuery = true)
    List<Object[]> sumRevenueByBranch();

    // Lấy 5 giao dịch gần nhất và tự động tính tổng tiền từ bảng SuatChieu
    @Query(value = "SELECT TOP 5 " +
            "h.id_HoaDon AS idHoaDon, " +
            "h.ngayThanhToan AS ngayThanhToan, " +
            "h.trangThai AS trangThai, " +
            "COALESCE(SUM(sc.giaMoiVe), 0) AS tongTien " +
            "FROM HoaDon h " +
            "LEFT JOIN Ve v ON h.id_HoaDon = v.id_HoaDon " +
            "LEFT JOIN SuatChieu sc ON v.id_SuatChieu = sc.id_SuatChieu " +
            "GROUP BY h.id_HoaDon, h.ngayThanhToan, h.trangThai " +
            "ORDER BY h.ngayThanhToan DESC", nativeQuery = true)
    List<HoaDonGiaoDichDTO> findTop5RecentWithTotal();
}

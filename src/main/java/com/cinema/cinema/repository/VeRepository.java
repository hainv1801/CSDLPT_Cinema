package com.cinema.cinema.repository;

import com.cinema.cinema.dto.response.DoanhThuNgayDTO;
import com.cinema.cinema.dto.response.DoanhThuPhimDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.cinema.cinema.entity.SuatChieu;
import com.cinema.cinema.entity.Ve;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VeRepository extends JpaRepository<Ve, Integer> {
        // Gọi Stored Procedure từ SQL Server
        @Query(value = "EXEC sp_DatGheAnToan @id_SuatChieu = :id_SuatChieu, @id_Ghe = :id_Ghe, @id_HoaDon = :id_HoaDon", nativeQuery = true)
        Integer datGheAnToan(
                        @Param("id_SuatChieu") Integer idSuatChieu,
                        @Param("id_Ghe") Integer idGhe,
                        @Param("id_HoaDon") Integer idHoaDon);

        // Đã sửa String thành Integer
        void deleteByHoaDon_IdHoaDon(Integer idHoaDon);

        // Đã sửa String thành Integer
        List<Ve> findByHoaDon_IdHoaDon(Integer idHoaDon);

        List<Ve> findBySuatChieu(SuatChieu suatChieu);

        // 1. Thống kê doanh thu theo phim
        @Query("SELECT p.id AS idPhim, p.ten AS tenPhim, COUNT(v.id) AS soVeDaBan, SUM(s.giaMoiVe) AS tongDoanhThu " +
                        "FROM Ve v JOIN v.hoaDon h JOIN v.suatChieu s JOIN s.phim p " +
                        "WHERE h.trangThai = 'DATHANHTOAN' " +
                        "AND h.ngayThanhToan >= :tuNgay AND h.ngayThanhToan <= :denNgay " +
                        "GROUP BY p.id, p.ten " +
                        "ORDER BY tongDoanhThu DESC")
        List<DoanhThuPhimDTO> thongKeDoanhThuTheoPhim(
                        @Param("tuNgay") LocalDateTime tuNgay,
                        @Param("denNgay") LocalDateTime denNgay);

        // 2. Thống kê doanh thu theo ngày
        @Query("SELECT FUNCTION('DATE', h.ngayThanhToan) AS ngay, COUNT(v.id) AS soVeDaBan, SUM(s.giaMoiVe) AS tongDoanhThu "
                        +
                        "FROM Ve v JOIN v.hoaDon h JOIN v.suatChieu s " +
                        "WHERE h.trangThai = 'DATHANHTOAN' " +
                        "AND h.ngayThanhToan >= :tuNgay AND h.ngayThanhToan <= :denNgay " +
                        "GROUP BY FUNCTION('DATE', h.ngayThanhToan) " +
                        "ORDER BY ngay ASC")
        List<DoanhThuNgayDTO> thongKeDoanhThuTheoNgay(
                        @Param("tuNgay") LocalDateTime tuNgay,
                        @Param("denNgay") LocalDateTime denNgay);

        // Đếm tổng số vé đã bán (không tính vé hủy)
        @Query("SELECT COUNT(v) FROM Ve v WHERE v.trangThai != 'DAHUY'")
        Long countVeDaBan();

        @Query(value = "EXEC sp_DatVe_PhanTan @id_HoaDon = :idHoaDon, @id_SuatChieu = :idSuatChieu, @danhSachGhe = :ghe, @maCoSoRap = :maCoSo", nativeQuery = true)
        void datVeQuaLinkedServer(
                @Param("idHoaDon") Integer idHoaDon,
                @Param("idSuatChieu") Integer idSuatChieu,
                @Param("ghe") String danhSachGhe,  // Sẽ nhận chuỗi "1,2,3"
                @Param("maCoSo") String maCoSoRap  // Ví dụ: "HN", "TH", "NA"
        );
}

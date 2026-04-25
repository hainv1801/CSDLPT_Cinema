package com.cinema.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.cinema.cinema.entity.Ve;

public interface VeRepository extends JpaRepository<Ve, Integer> {
    // Gọi Stored Procedure từ SQL Server
    @Procedure(procedureName = "sp_DatGheAnToan")
    Integer datGheAnToan(
            @Param("id_SuatChieu") Integer idSuatChieu,
            @Param("id_Ghe") Integer idGhe,
            @Param("id_HoaDon") String idHoaDon);

    // Dùng để xóa vé khi người dùng hủy hoặc thanh toán thất bại
    void deleteByHoaDon_IdHoaDon(String idHoaDon);

    List<Ve> findByHoaDon_IdHoaDon(String idHoaDon);
}

package com.cinema.cinema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.cinema.cinema.dto.request.ReqDatVeDTO;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.cinema.ma-co-so}")
    private String maCoSoHienTai;

    @Value("${app.cinema.linked-server:}")
    private String linkedServerName;

    public Integer datVe(ReqDatVeDTO req) {
        if (req.getDanhSachIdGhe() == null || req.getDanhSachIdGhe().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất 1 ghế!");
        }

        // 1. Lấy mã cơ sở của Rạp đang chiếu Suất này
        String maCoSoCuaRap = jdbcTemplate.queryForObject(
                "SELECT pc.MaCoSo FROM SuatChieu sc JOIN PhongChieu pc ON sc.id_PhongChieu = pc.id_PhongChieu WHERE sc.id_SuatChieu = ?",
                String.class, req.getIdSuatChieu()
        );

        String chuoiIdGhe = req.getDanhSachIdGhe().stream().map(String::valueOf).collect(Collectors.joining(","));
        String sqlCall;

        // 2. Logic định tuyến thông minh
        if (maCoSoHienTai.trim().equalsIgnoreCase(maCoSoCuaRap.trim()) || maCoSoHienTai.trim().equalsIgnoreCase("KV_00")) {
            // Mua rạp local -> Gọi DB local (Không qua Linked Server)
            sqlCall = "EXEC [dbo].[sp_DatVeToanQuoc] ?, ?, ?, ?";
        } else {
            // Mua rạp chi nhánh khác -> Bắn qua Linked Server
            sqlCall = "EXEC [" + linkedServerName + "].[rapchieuphim].[dbo].[sp_DatVeToanQuoc] ?, ?, ?, ?";
        }

        try {
            Integer result = jdbcTemplate.queryForObject(
                    sqlCall, Integer.class,
                    req.getIdSuatChieu(), req.getIdNguoiDung(), req.getIdPhuongThucThanhToan(), chuoiIdGhe
            );

            if (result == null || result == 0) throw new RuntimeException("Lỗi hệ thống CSDL.");
            if (result == -1) throw new RuntimeException("Rất tiếc, ghế bạn chọn vừa có người đặt!");
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi giao dịch phân tán: " + e.getMessage());
        }
    }
}
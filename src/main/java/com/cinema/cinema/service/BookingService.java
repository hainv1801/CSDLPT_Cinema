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

    // ĐÃ XÓA @Transactional để nhường quyền quản lý ACID hoàn toàn cho SQL Server
    public Integer datVe(ReqDatVeDTO req) {

        if (req.getDanhSachIdGhe() == null || req.getDanhSachIdGhe().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất 1 ghế!");
        }

        String chuoiIdGhe = req.getDanhSachIdGhe().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sqlCall;
        if ("KV_00".equals(maCoSoHienTai)) {
            // Máy chủ Trung tâm
            sqlCall = "EXEC [dbo].[sp_DatVeToanQuoc] ?, ?, ?, ?";
        } else {
            // Máy trạm chi nhánh -> Gọi lên Trung tâm
            sqlCall = "EXEC [" + linkedServerName + "].[rapchieuphim].[dbo].[sp_DatVeToanQuoc] ?, ?, ?, ?";
        }

        try {
            Integer result = jdbcTemplate.queryForObject(
                    sqlCall,
                    Integer.class,
                    req.getIdSuatChieu(),
                    req.getIdNguoiDung(),
                    req.getIdPhuongThucThanhToan(),
                    chuoiIdGhe
            );

            if (result == null || result == 0) {
                throw new RuntimeException("Lỗi hệ thống cơ sở dữ liệu khi đặt vé.");
            } else if (result == -1) {
                // Bạn có thể đổi lại thành SeatAlreadyBookedException như cũ cho chuẩn!
                throw new RuntimeException("Rất tiếc, một số ghế bạn chọn vừa có người đặt mất. Vui lòng chọn lại!");
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi giao dịch qua Linked Server: " + e.getMessage());
        }
    }
}
package com.cinema.cinema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public String bookTicket(Integer suatChieuId, Integer gheId, Integer hoaDonId, Integer userId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_DatVePhanTan");

            SqlParameterSource in = new MapSqlParameterSource()
                    .addValue("id_SuatChieu", suatChieuId)
                    .addValue("id_Ghe", gheId)
                    .addValue("id_HoaDon", hoaDonId)
                    .addValue("id_NguoiDung", userId);

            jdbcCall.execute(in);
            return "Đặt vé thành công!";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }
}
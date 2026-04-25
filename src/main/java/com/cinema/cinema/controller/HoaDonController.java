package com.cinema.cinema.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema.dto.request.ReqHoaDon;
import com.cinema.cinema.dto.response.ResHoaDon;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.service.HoaDonService;
import com.cinema.cinema.service.NguoiDungService;
import com.cinema.cinema.util.SecurityUtil;

@RestController
@RequestMapping("/api/payments")

public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final NguoiDungService nguoiDungService;

    public HoaDonController(HoaDonService hoaDonService, NguoiDungService nguoiDungService) {
        this.hoaDonService = hoaDonService;
        this.nguoiDungService = nguoiDungService;
    }

    // API Khách hàng bấm nút "Thanh Toán" trên Frontend
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('KHACHHANG')") // Yêu cầu đăng nhập JWT
    public ResponseEntity<ResHoaDon> checkout(
            @RequestBody ReqHoaDon request) {
        String taiKhoan = SecurityUtil.getCurrentUserLogin().orElse("");
        NguoiDung currentUserDB = this.nguoiDungService.handleGetUserByUsername(taiKhoan);

        // Gọi service xử lý thanh toán 1 bước
        ResHoaDon response = hoaDonService.thanhToanMock(currentUserDB.getIdNguoiDung(), request);

        return ResponseEntity.ok(response);
    }
}
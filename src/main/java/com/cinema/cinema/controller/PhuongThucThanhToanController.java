package com.cinema.cinema.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema.entity.PhuongThucThanhToan;
import com.cinema.cinema.service.PhuongThucThanhToanService;
import com.cinema.cinema.util.ApiMessage;

@RestController
@RequestMapping("/api/v1/payment-methods")
public class PhuongThucThanhToanController {
    private final PhuongThucThanhToanService phuongThucThanhToanService;

    public PhuongThucThanhToanController(PhuongThucThanhToanService phuongThucThanhToanService) {
        this.phuongThucThanhToanService = phuongThucThanhToanService;
    }

    @GetMapping
    @ApiMessage("Lấy danh sách phương thức thanh toán thành công")
    public ResponseEntity<List<PhuongThucThanhToan>> getPaymentMethods() {
        return ResponseEntity.ok(this.phuongThucThanhToanService.handleGetActivePaymentMethods());
    }
}

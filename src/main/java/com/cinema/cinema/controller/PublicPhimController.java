package com.cinema.cinema.controller;

import com.cinema.cinema.dto.response.ResPhimDTO;
import com.cinema.cinema.service.PhimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/phim")
@CrossOrigin("*")
public class PublicPhimController {

    @Autowired
    private PhimService phimService;

    // Lấy danh sách Phim Đang Chiếu
    @GetMapping("/dang-chieu")
    public ResponseEntity<List<ResPhimDTO>> getPhimDangChieu() {
        List<ResPhimDTO> danhSach = phimService.getDanhSachPhimDangChieu();
        return ResponseEntity.ok(danhSach);
    }

    //Lấy danh sách Phim Sắp Chiếu
    @GetMapping("/sap-chieu")
    public ResponseEntity<List<ResPhimDTO>> getPhimSapChieu() {
        List<ResPhimDTO> danhSach = phimService.getDanhSachPhimSapChieu();
        return ResponseEntity.ok(danhSach);
    }
}
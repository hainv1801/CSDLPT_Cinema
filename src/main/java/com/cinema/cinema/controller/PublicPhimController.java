package com.cinema.cinema.controller;

import com.cinema.cinema.dto.response.ResChiTietPhimDTO;
import com.cinema.cinema.dto.response.ResPhimDTO;
import com.cinema.cinema.entity.TheLoai;
import com.cinema.cinema.repository.TheLoaiRepository;
import com.cinema.cinema.service.PhimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/public/phim")
@CrossOrigin("*")
public class PublicPhimController {

    @Autowired
    private PhimService phimService;

    @Autowired
    private TheLoaiRepository theLoaiRepository;

    // Lấy danh sách Phim Đang Chiếu
    @GetMapping("/dang-chieu")
    public ResponseEntity<List<ResPhimDTO>> getPhimDangChieu() {
        List<ResPhimDTO> danhSach = phimService.getDanhSachPhimDangChieu();
        return ResponseEntity.ok(danhSach);
    }

    // Lấy danh sách Phim Sắp Chiếu
    @GetMapping("/sap-chieu")
    public ResponseEntity<List<ResPhimDTO>> getPhimSapChieu() {
        List<ResPhimDTO> danhSach = phimService.getDanhSachPhimSapChieu();
        return ResponseEntity.ok(danhSach);
    }

    // Lấy chi tiết 1 bộ phim
    @GetMapping("/{id}")
    public ResponseEntity<ResChiTietPhimDTO> getChiTietPhim(@PathVariable("id") Integer id) {
        ResChiTietPhimDTO chiTiet = phimService.getChiTietPhim(id);
        return ResponseEntity.ok(chiTiet);
    }

    @GetMapping("/the-loai")
    public ResponseEntity<List<TheLoai>> getAllTheLoai() {
        List<TheLoai> list = theLoaiRepository.findAll();
        return ResponseEntity.ok(list);
    }

}
package com.cinema.cinema.controller;

import com.cinema.cinema.dto.response.DoanhThuNgayDTO;
import com.cinema.cinema.dto.response.DoanhThuPhimDTO;
import com.cinema.cinema.repository.HoaDonRepository;
import com.cinema.cinema.repository.NguoiDungRepository;
import com.cinema.cinema.repository.PhimRepository;
import com.cinema.cinema.repository.VeRepository;
import com.cinema.cinema.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/thong-ke")
@CrossOrigin("*")
public class AdminThongKeController {

    @Autowired
    private ThongKeService thongKeService;

    @GetMapping("/phim")
    public ResponseEntity<List<DoanhThuPhimDTO>> thongKeTheoPhim(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.layDoanhThuPhim(tuNgay, denNgay));
    }

    @GetMapping("/ngay")
    public ResponseEntity<List<DoanhThuNgayDTO>> thongKeTheoNgay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.layDoanhThuNgay(tuNgay, denNgay));
    }

    @GetMapping("/tong-quan")
    public ResponseEntity<?> getThongKe() {
        return ResponseEntity.ok(this.thongKeService.getThongKeTongQuan());
    }

}

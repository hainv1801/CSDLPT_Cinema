package com.cinema.cinema.controller;

import com.cinema.cinema.entity.Ghe;
import com.cinema.cinema.repository.GheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ghe")
@CrossOrigin("*")
public class AdminGheController {

    @Autowired
    private GheRepository gheRepository;

    //Cập nhật trạng thái 1 ghế bất kì
    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<?> updateTrangThaiGhe(
            @PathVariable Integer id,
            @RequestParam String trangThai) {

        // 1. Tìm ghế trong Database
        Ghe ghe = gheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế với ID này!"));

        // 2. Kiểm tra dữ liệu đầu vào (Validation)
        if (!trangThai.equals("TOT") && !trangThai.equals("BAOTRI")) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ. Chỉ chấp nhận 'TOT' hoặc 'BAOTRI'");
        }

        // 3. Cập nhật và lưu lại
        ghe.setTrangThai(trangThai);
        gheRepository.save(ghe);

        return ResponseEntity.ok(ghe);
    }
}

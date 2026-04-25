package com.cinema.cinema.controller;

import com.cinema.cinema.dto.request.ReqSuatChieuDTO;
import com.cinema.cinema.dto.response.ResSuatChieuDTO;
import com.cinema.cinema.service.SuatChieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/suat-chieu")
public class AdminSuatChieuController {

    @Autowired private SuatChieuService suatChieuService;

    //1. Lấy tât cả suất chiếu
    @GetMapping
    public ResponseEntity<List<ResSuatChieuDTO>> getAllSuatChieu() {
        List<ResSuatChieuDTO> danhSach = suatChieuService.getAll();
        return ResponseEntity.ok(danhSach); // HTTP 200 OK
    }

    //2. Lấy chi tiết 1 suất chiếu
    @GetMapping("/{id}")
    public ResponseEntity<ResSuatChieuDTO> getSuatChieuById(@PathVariable Integer id) {
        ResSuatChieuDTO suatChieu = suatChieuService.getById(id);
        return ResponseEntity.ok(suatChieu); // HTTP 200 OK
    }

    //3. Tạo suất chiếu mới
    @PostMapping
    public ResponseEntity<ResSuatChieuDTO> createSuatChieu(@RequestBody ReqSuatChieuDTO request) {
        ResSuatChieuDTO newSuatChieu = suatChieuService.createSuatChieu(request);
        return new ResponseEntity<>(newSuatChieu, HttpStatus.CREATED); // HTTP 201 Created
    }

    //4. Cập nhật suất chiếu
    @PutMapping("/{id}")
    public ResponseEntity<ResSuatChieuDTO> updateSuatChieu(
            @PathVariable Integer id,
            @RequestBody ReqSuatChieuDTO request) {

        ResSuatChieuDTO updatedSuatChieu = suatChieuService.updateSuatChieu(id, request);
        return ResponseEntity.ok(updatedSuatChieu); // HTTP 200 OK
    }

    //5. Xóa suất chiếu
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSuatChieu(@PathVariable Integer id) {
        suatChieuService.deleteSuatChieu(id);
        return ResponseEntity.ok("Đã xóa suất chiếu thành công!"); // HTTP 200 OK
    }
}

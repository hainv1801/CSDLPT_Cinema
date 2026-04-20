package com.cinema.cinema.controller;

import com.cinema.cinema.dto.request.ReqPhimDTO;
import com.cinema.cinema.dto.response.ResPhimDTO;
import com.cinema.cinema.service.PhimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/phim")
@CrossOrigin("*")
public class AdminPhimController {

    @Autowired
    private PhimService phimService;

    //1. Lấy danh sách tất cả các phim
    @GetMapping
    public ResponseEntity<List<ResPhimDTO>> getAllPhim() {
        List<ResPhimDTO> danhSachPhim = phimService.getAll();
        return ResponseEntity.ok(danhSachPhim); // HTTP 200 OK
    }

    //2. Lấy 1 bộ phim
    @GetMapping("/{id}")
    public ResponseEntity<ResPhimDTO> getPhimById(@PathVariable Integer id) {
        ResPhimDTO phim = phimService.getById(id);
        return ResponseEntity.ok(phim); // HTTP 200 OK
    }

    //3. Thêm bộ phim mới
    @PostMapping
    public ResponseEntity<ResPhimDTO> createPhim(@RequestBody ReqPhimDTO request) {
        ResPhimDTO newPhim = phimService.create(request);
        return new ResponseEntity<>(newPhim, HttpStatus.CREATED); // HTTP 201 Created
    }

    //4. Cập nhật thông tin phim
    @PutMapping("/{id}")
    public ResponseEntity<ResPhimDTO> updatePhim(
            @PathVariable Integer id,
            @RequestBody ReqPhimDTO request) {

        ResPhimDTO updatedPhim = phimService.update(id, request);
        return ResponseEntity.ok(updatedPhim); // HTTP 200 OK
    }

    //5. Xóa bộ phim
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePhim(@PathVariable Integer id) {
        phimService.delete(id);
        return ResponseEntity.ok("Đã xóa bộ phim thành công!"); // HTTP 200 OK
    }

}

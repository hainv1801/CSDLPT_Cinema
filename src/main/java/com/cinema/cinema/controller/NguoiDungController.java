package com.cinema.cinema.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema.dto.request.ReqUpdateUserDTO;
import com.cinema.cinema.dto.response.ResUserDTO;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.service.NguoiDungService;
import com.cinema.cinema.util.ApiMessage;
import com.cinema.cinema.util.SecurityUtil;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    public NguoiDungController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    // // 1. API dành riêng cho QUẢN LÝ (Lấy danh sách tất cả user)
    // @GetMapping("/all")
    // @ApiMessage("Lấy danh sách toàn bộ người dùng")
    // @PreAuthorize("hasRole('QUANLY')") // CHỈ CÓ ROLE_QUANLY MỚI ĐƯỢC VÀO
    // public ResponseEntity<String> getAllUsers() {
    // return ResponseEntity.ok("Xin chào sếp! Đây là danh sách toàn bộ khách hàng
    // và nhân viên.");
    // }

    @GetMapping("/me")
    @ApiMessage("Lấy thông tin cá nhân thành công")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResUserDTO> getMyProfile() {
        // Lấy username từ Token
        String taiKhoan = SecurityUtil.getCurrentUserLogin().orElse("");

        NguoiDung user = this.nguoiDungService.handleGetUserByUsername(taiKhoan);

        return ResponseEntity.ok(this.nguoiDungService.convertToResUserDTO(user));
    }

    @PutMapping("/me")
    @ApiMessage("Cập nhật thông tin cá nhân thành công")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResUserDTO> updateMyProfile(@Valid @RequestBody ReqUpdateUserDTO reqUpdateUserDTO) {
        // Lấy username từ Token
        String taiKhoan = SecurityUtil.getCurrentUserLogin().orElse("");

        NguoiDung updatedUser = this.nguoiDungService.handleUpdateUser(reqUpdateUserDTO, taiKhoan);

        return ResponseEntity.ok(this.nguoiDungService.convertToResUserDTO(updatedUser));
    }
}

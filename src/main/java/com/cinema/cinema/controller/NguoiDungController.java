package com.cinema.cinema.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema.dto.request.ReqUpdateUserDTO;
import com.cinema.cinema.dto.response.ResUserDTO;
import com.cinema.cinema.dto.response.ResultPaginationDTO;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.exception.IdInvalidException;
import com.cinema.cinema.service.NguoiDungService;
import com.cinema.cinema.util.ApiMessage;
import com.cinema.cinema.util.SecurityUtil;

import jakarta.validation.Valid;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    public NguoiDungController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    @GetMapping("")
    @ApiMessage("fetch all users")
    @PreAuthorize("hasRole('QUANLY')")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.nguoiDungService.fetchAllUser(spec, pageable));
    }

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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUANLY')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) throws IdInvalidException {
        NguoiDung currentUser = this.nguoiDungService.findById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User khong ton tai");
        }
        nguoiDungService.deleteById(id); // Cần viết thêm hàm deleteById trong Service
        return ResponseEntity.ok(null);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('QUANLY')")
    public ResponseEntity<ResUserDTO> updateRole(@PathVariable Integer id, @RequestParam String role)
            throws IdInvalidException {
        NguoiDung currentUser = this.nguoiDungService.findById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User khong ton tai");
        }
        return ResponseEntity.ok(nguoiDungService.convertToResUserDTO(this.nguoiDungService.updateUserRole(id, role)));
    }
}

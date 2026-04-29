package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cinema.cinema.entity.NguoiDung;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer>, JpaSpecificationExecutor<NguoiDung> {
    NguoiDung findByTaiKhoan(String taiKhoan);

    // Hàm này dùng để kiểm tra email tồn tại chưa khi đăng ký
    boolean existsByEmail(String email);

    boolean existsByTaiKhoan(String taiKhoan);

    NguoiDung findByRefreshTokenAndTaiKhoan(String token, String taiKhoan);

}

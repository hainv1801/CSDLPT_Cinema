package com.cinema.cinema.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cinema.cinema.dto.request.ReqUpdateUserDTO;
import com.cinema.cinema.dto.response.ResUserDTO;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.repository.NguoiDungRepository;

@Service
public class NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public NguoiDungService(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public NguoiDung handleCreateUser(NguoiDung user) {
        // Mã hóa mật khẩu trước khi lưu
        String hashPassword = this.passwordEncoder.encode(user.getMatKhau());
        user.setMatKhau(hashPassword);

        // Mặc định khách đăng ký mới sẽ có quyền ROLE_KHACHHANG
        if (user.getVaiTro() == null || user.getVaiTro().isEmpty()) {
            user.setVaiTro("KHACHHANG");
        }

        return this.nguoiDungRepository.save(user);
    }

    public NguoiDung handleGetUserByUsername(String taiKhoan) {
        return this.nguoiDungRepository.findByTaiKhoan(taiKhoan);
    }

    public boolean isTaiKhoanExist(String taiKhoan) {
        return this.nguoiDungRepository.existsByTaiKhoan(taiKhoan);
    }

    public boolean isEmailExist(String email) {
        return this.nguoiDungRepository.existsByEmail(email);
    }

    public void updateUserToken(String token, String taiKhoan) {
        NguoiDung currentUser = this.handleGetUserByUsername(taiKhoan);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.nguoiDungRepository.save(currentUser);
        }
    }

    public NguoiDung getUserByRefreshTokenAndTaiKhoan(String token, String taiKhoan) {
        return this.nguoiDungRepository.findByRefreshTokenAndTaiKhoan(token,
                taiKhoan);
    }

    public NguoiDung handleUpdateUser(ReqUpdateUserDTO req, String taiKhoan) {
        // 1. Tìm user hiện tại
        NguoiDung currentUser = this.handleGetUserByUsername(taiKhoan);

        if (currentUser != null) {
            // 2. Thấy Postman gửi gì lên thì đè cái đó vào, không gửi thì giữ nguyên đồ cũ
            if (req.getHoTen() != null) {
                currentUser.setHoTen(req.getHoTen());
            }
            if (req.getEmail() != null) {
                currentUser.setEmail(req.getEmail());
            }
            if (req.getSdt() != null) {
                currentUser.setSdt(req.getSdt());
            }

            if (req.getNgaySinh() != null) {
                currentUser.setNgaySinh(req.getNgaySinh());
            }
            // 3. Đem chính ông user vừa được đè thông tin mới này đi cất
            return this.nguoiDungRepository.save(currentUser);
        }
        return null;
    }

    public ResUserDTO convertToResUserDTO(NguoiDung user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getIdNguoiDung());
        res.setTaiKhoan(user.getTaiKhoan());
        res.setHoTen(user.getHoTen());
        res.setEmail(user.getEmail());
        res.setSdt(user.getSdt());
        res.setVaiTro(user.getVaiTro());
        res.setNgaySinh(user.getNgaySinh());
        return res;
    }
}

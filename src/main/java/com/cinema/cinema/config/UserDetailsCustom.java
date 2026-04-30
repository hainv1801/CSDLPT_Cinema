package com.cinema.cinema.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.service.NguoiDungService;

// Đánh dấu đây là một Bean cốt lõi cho việc quản lý User của Security
@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {

    private final NguoiDungService nguoiDungService;

    public UserDetailsCustom(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm NguoiDung trong cơ sở dữ liệu
        NguoiDung user = this.nguoiDungService.handleGetUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Tài khoản/Mật khẩu không hợp lệ");
        }

        // 2. Gắn thêm chữ ROLE_ để Spring Security nhận diện quyền
        String userRole = "ROLE_" + user.getVaiTro();

        // 3. Trả về đối tượng CustomUserDetails
        return new CustomUserDetails(
                user.getTaiKhoan(),
                user.getMatKhau(),
                Collections.singletonList(new SimpleGrantedAuthority(userRole)),
                user.getMaCoSo());
    }
}
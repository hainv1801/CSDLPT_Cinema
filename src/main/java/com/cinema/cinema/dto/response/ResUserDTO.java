package com.cinema.cinema.dto.response;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUserDTO {
    private long id;
    private String taiKhoan;
    private String hoTen;
    private String email;
    private String sdt;
    private String vaiTro;
    private LocalDate ngaySinh;
}
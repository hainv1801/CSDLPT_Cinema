package com.cinema.cinema.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateUserDTO {
    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "SDT không được để trống")
    private String sdt;

    private LocalDate ngaySinh;
}
package com.cinema.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "NguoiDung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_NguoiDung")
    private Integer idNguoiDung;

    @NotBlank(message = "Tài khoản không được để trống")
    @Column(name = "taiKhoan", nullable = false, unique = true)
    private String taiKhoan;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "matKhau", nullable = false)
    private String matKhau;

    @NotBlank(message = "Tên không được để trống")
    @Column(name = "hoTen", nullable = false)
    private String hoTen;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @NotBlank(message = "Sđt không được để trống")
    @Column(name = "SDT", nullable = false)
    private String sdt;

    @NotBlank(message = "Email không được để trống")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "vaiTro", nullable = false)
    private String vaiTro;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String refreshToken;
}
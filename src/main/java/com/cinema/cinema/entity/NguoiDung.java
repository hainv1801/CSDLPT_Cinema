package com.cinema.cinema.entity;

import jakarta.persistence.*;
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

    @Column(name = "taiKhoan", nullable = false, unique = true)
    private String taiKhoan;

    @Column(name = "matKhau", nullable = false)
    private String matKhau;

    @Column(name = "hoTen", nullable = false)
    private String hoTen;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "SDT", nullable = false)
    private String sdt;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "vaiTro", nullable = false)
    private String vaiTro;
}
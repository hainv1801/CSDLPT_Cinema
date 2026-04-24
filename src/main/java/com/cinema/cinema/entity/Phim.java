package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "Phim")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Phim")
    private Integer idPhim;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "ngayPhatHanh", nullable = false)
    private LocalDate ngayPhatHanh;

    @Column(name = "thoiLuong", nullable = false)
    private Double thoiLuong;

    @Column(name = "ngonNguChinh", nullable = false)
    private String ngonNguChinh;

    @Column(name = "noiDung", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    // Tự động xử lý bảng trung gian TheLoaiPhim (Quan hệ N-N)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "TheLoaiPhim",
            joinColumns = @JoinColumn(name = "id_Phim"),
            inverseJoinColumns = @JoinColumn(name = "id_TheLoai")
    )
    private Set<TheLoai> theLoais = new HashSet<>();
}
package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Ghe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ghe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Ghe")
    private Integer idGhe;

    @Column(name = "hang", nullable = false)
    private Integer hang;

    @Column(name = "cot", nullable = false)
    private Integer cot;

    @Column(name = "trangThai", nullable = false)
    private String trangThai;

    // Khóa ngoại liên kết với bảng PhongChieu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_PhongChieu", nullable = false)
    private PhongChieu phongChieu;
}
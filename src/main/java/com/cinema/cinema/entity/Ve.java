package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Ve")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_VeXemPhim")
    private Integer idVeXemPhim;

    @Column(name = "trangThai", nullable = false)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_SuatChieu", nullable = false)
    private SuatChieu suatChieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_HoaDon", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Ghe", nullable = false)
    private Ghe ghe;
}
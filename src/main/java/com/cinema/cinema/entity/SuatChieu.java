package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SuatChieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuatChieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_SuatChieu")
    private Integer idSuatChieu;

    @Column(name = "giaMoiVe", nullable = false)
    private Integer giaMoiVe;

    @Column(name = "thoiGianBatDau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_PhongChieu", nullable = false)
    private PhongChieu phongChieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Phim", nullable = false)
    private Phim phim;

}
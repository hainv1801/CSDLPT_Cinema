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

    @Column(name = "daDat")
    private Integer daDAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_PhongChieu", nullable = false)
    private PhongChieu phongChieu;

    public String getTen() {
        return "" + (char) ('A' + this.getHang() - 1) + this.getCot();
    }
}
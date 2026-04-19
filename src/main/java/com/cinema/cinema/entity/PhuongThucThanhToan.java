package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PhuongThucThanhToan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhuongThucThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_PhuongThucThanhToan")
    private Integer idPhuongThucThanhToan;

    @Column(name = "noiDung", nullable = false)
    private String noiDung;
}
package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Rap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Rap")
    private Integer idRap;

    @Column(name = "tenRap", nullable = false)
    private String tenRap;

    @Column(name = "diaChi", nullable = false)
    private String diaChi;

    @Column(name = "khuVuc", nullable = false)
    private String khuVuc;
}
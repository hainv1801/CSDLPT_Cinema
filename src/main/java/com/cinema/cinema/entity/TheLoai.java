package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TheLoai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheLoai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_TheLoai")
    private Integer idTheLoai;

    @Column(name = "noiDung", nullable = false)
    private String noiDung;
}
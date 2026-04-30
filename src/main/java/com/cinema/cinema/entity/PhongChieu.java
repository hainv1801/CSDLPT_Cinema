package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PhongChieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhongChieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_PhongChieu")
    private Integer idPhongChieu;

    @Column(name = "trangThai", nullable = false)
    private String trangThai;

    @Column(name = "sucChua", nullable = false)
    private Integer sucChua;

    // Khóa ngoại liên kết với bảng Rap
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Rap", nullable = false)
    private Rap rap;

    @Column(name = "MaCoSo")
    private String maCoSo;
}

package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_HoaDon")
    private Integer idHoaDon;

    @Column(name = "ngayThanhToan", nullable = false)
    private LocalDateTime ngayThanhToan;

    @Column(name = "trangThai", nullable = false)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_PhuongThucThanhToan", nullable = false)
    private PhuongThucThanhToan phuongThucThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_NguoiDung", nullable = false)
    private NguoiDung nguoiDung;
}

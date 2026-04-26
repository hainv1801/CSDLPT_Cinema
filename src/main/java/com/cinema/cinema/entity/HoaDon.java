package com.cinema.cinema.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "HoaDon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 👉 Đổi về IDENTITY
    @Column(name = "id_HoaDon") // Xóa cái generator UUID đi
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

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Ve> ves;
}

package com.cinema.cinema.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResSuatChieuDTO {
    private Integer id;
    private Integer giaMoiVe;
    private LocalDateTime thoiGianBatDau;
    private Integer idPhim;
    private String tenPhim;
    private Integer idPhongChieu;
    private Integer idRap;
    private String tenRap;
}

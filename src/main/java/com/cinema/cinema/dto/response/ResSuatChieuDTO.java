package com.cinema.cinema.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResSuatChieuDTO {
    private Integer id;
    private Integer giaMoiVe;
    private LocalDateTime thoiGianBatDau;
    private String tenPhim;
    private Integer idPhong;
    private String tenRap;
}


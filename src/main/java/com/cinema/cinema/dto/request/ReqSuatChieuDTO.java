package com.cinema.cinema.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReqSuatChieuDTO {
    private Integer giaMoiVe;
    private LocalDateTime thoiGianBatDau;
    private Integer idPhongChieu;
    private Integer idPhim;
}

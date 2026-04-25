package com.cinema.cinema.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqHoaDon {
    private Integer idSuatChieu;
    private List<Integer> danhSachIdGhe;
    private Integer idPhuongThucThanhToan;
}

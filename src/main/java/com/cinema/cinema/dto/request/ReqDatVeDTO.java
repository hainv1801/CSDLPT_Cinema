package com.cinema.cinema.dto.request; // Sửa lại package cho đúng

import lombok.Data;
import java.util.List;

@Data
public class ReqDatVeDTO {
    private Integer idSuatChieu;
    private List<Integer> danhSachIdGhe;
    private Integer idNguoiDung;
    private Integer idPhuongThucThanhToan;
}
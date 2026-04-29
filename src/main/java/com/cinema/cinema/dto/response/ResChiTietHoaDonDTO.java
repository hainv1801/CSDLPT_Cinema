package com.cinema.cinema.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResChiTietHoaDonDTO {
    private Integer idHoaDon;
    private String tenKhachHang;
    private String sdtKhachHang;
    private String tenPhim;
    private String rapChieu;
    private String phongChieu;
    private String phuongThucThanhToan;
    private LocalDateTime gioChieu; // Định dạng kiểu String hoặc LocalDateTime
    private List<String> danhSachGhe; // Ví dụ: ["A1", "A2", "B5"]
    private int tongTien;
    private String trangThai;
}
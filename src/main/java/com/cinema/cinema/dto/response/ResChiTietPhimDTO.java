package com.cinema.cinema.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ResChiTietPhimDTO {
    private Integer idPhim;
    private String ten;
    private LocalDate ngayPhatHanh;
    private Integer thoiLuong;
    private String ngonNguChinh;
    private String noiDung;

    private List<String> danhSachTheLoai;
}

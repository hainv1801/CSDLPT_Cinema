package com.cinema.cinema.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResHoaDonDTO {
    private int idHoaDon;
    private String trangThai;
    private LocalDateTime ngayThanhToan;
    private String phuongThucThanhToan;
    private String email;
    private int tongTien;
}

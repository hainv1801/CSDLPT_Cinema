package com.cinema.cinema.dto.response;

import java.time.LocalDateTime;

public interface HoaDonGiaoDichDTO {
    Integer getIdHoaDon();

    LocalDateTime getNgayThanhToan();

    String getTrangThai();

    int getTongTien(); // Cột này sẽ chứa kết quả của hàm SUM()
}
package com.cinema.cinema.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ResPhimDTO {
    private Integer id;
    private String ten;
    private LocalDate ngayPhatHanh;
    private Integer thoiLuong;
    private String ngonNguChinh;
    private String noiDung;
    private String poster;
    // Khi trả về, ta trả chi tiết các thể loại để Frontend in ra màn hình
    private List<String> tenTheLoais;
}

@Data
class TheLoaiDTO {
    private Integer id;
    private String noiDung;
}

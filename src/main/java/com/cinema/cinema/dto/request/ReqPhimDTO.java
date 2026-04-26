package com.cinema.cinema.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReqPhimDTO {
    private String ten;
    private LocalDate ngayPhatHanh;
    private Integer thoiLuong;
    private String ngonNguChinh;
    private String noiDung;
    private String poster;
    // Frontend chỉ cần gửi lên danh sách ID của thể loại: [1, 3, 5]
    private List<Integer> theLoaiIds;
}

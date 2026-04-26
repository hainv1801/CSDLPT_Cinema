package com.cinema.cinema.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResGheDTO {
    private Integer idGhe;
    private String tenGhe;
    private Integer daDat; // 1 = Đã bán (màu xám), 0 = Trống (màu trắng)
}
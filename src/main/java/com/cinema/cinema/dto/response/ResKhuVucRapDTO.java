package com.cinema.cinema.dto.response;

import com.cinema.cinema.entity.Rap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResKhuVucRapDTO {
    private String tenKhuVuc;
    private List<Rap> danhSachRap;
}

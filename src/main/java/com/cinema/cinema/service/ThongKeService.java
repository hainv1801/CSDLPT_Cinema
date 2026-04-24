package com.cinema.cinema.service;

import com.cinema.cinema.dto.response.DoanhThuNgayDTO;
import com.cinema.cinema.dto.response.DoanhThuPhimDTO;
import com.cinema.cinema.repository.VeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ThongKeService {

    @Autowired
    private VeRepository veRepository;

    //1. Lấy doanh thu phim
    public List<DoanhThuPhimDTO> layDoanhThuPhim(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.atTime(LocalTime.MAX);
        return veRepository.thongKeDoanhThuTheoPhim(start, end);
    }

    //2. Lấy doanh thu ngày
    public List<DoanhThuNgayDTO> layDoanhThuNgay(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.atTime(LocalTime.MAX);
        return veRepository.thongKeDoanhThuTheoNgay(start, end);
    }
}

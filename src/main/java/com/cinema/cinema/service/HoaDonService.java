package com.cinema.cinema.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.cinema.dto.request.ReqHoaDon;
import com.cinema.cinema.dto.response.ResHoaDon;
import com.cinema.cinema.entity.HoaDon;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.entity.PhuongThucThanhToan;
import com.cinema.cinema.entity.SuatChieu;
import com.cinema.cinema.repository.HoaDonRepository;
import com.cinema.cinema.repository.NguoiDungRepository;
import com.cinema.cinema.repository.PhuongThucThanhToanRepository;
import com.cinema.cinema.repository.SuatChieuRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;

    public HoaDonService(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;

    }
}

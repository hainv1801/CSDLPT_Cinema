package com.cinema.cinema.service;

import com.cinema.cinema.dto.request.ReqPhongChieuDTO;
import com.cinema.cinema.entity.Ghe;
import com.cinema.cinema.entity.PhongChieu;
import com.cinema.cinema.entity.Rap;
import com.cinema.cinema.repository.GheRepository;
import com.cinema.cinema.repository.PhongChieuRepository;
import com.cinema.cinema.repository.RapRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhongChieuService {

    @Autowired private PhongChieuRepository phongChieuRepository;
    @Autowired private GheRepository gheRepository;
    @Autowired private RapRepository rapRepository;

    @Transactional
    public PhongChieu taoPhongChieuVaSinhGhe(ReqPhongChieuDTO request) {
        // 1. Kiểm tra Rạp có tồn tại không
        Rap rap = rapRepository.findById(request.getIdRap())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Rạp với ID: " + request.getIdRap()));

        // 2. Tạo Phòng Chiếu
        PhongChieu phong = new PhongChieu();
        phong.setTrangThai("DANGHOATDONG");
        phong.setRap(rap);

        phong.setSucChua(request.getSoHang() * request.getSoCot());

        PhongChieu phongDaLuu = phongChieuRepository.save(phong);

        List<Ghe> danhSachGhe = new ArrayList<>();

        for (int i = 1; i <= request.getSoHang(); i++) {
            for (int j = 1; j <= request.getSoCot(); j++) {
                Ghe ghe = new Ghe();
                ghe.setHang(i);
                ghe.setCot(j);
                ghe.setTrangThai("TOT");
                ghe.setPhongChieu(phongDaLuu);

                danhSachGhe.add(ghe);
            }
        }

        gheRepository.saveAll(danhSachGhe);

        return phongDaLuu;
    }
}

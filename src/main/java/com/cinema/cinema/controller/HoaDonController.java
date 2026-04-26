package com.cinema.cinema.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema.dto.request.ReqHoaDon;
import com.cinema.cinema.dto.response.ResHoaDon;
import com.cinema.cinema.entity.HoaDon;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.entity.Ve;
import com.cinema.cinema.repository.HoaDonRepository;
import com.cinema.cinema.service.HoaDonService;
import com.cinema.cinema.service.NguoiDungService;
import com.cinema.cinema.util.SecurityUtil;

@RestController
@RequestMapping("/api/payments")

public class HoaDonController {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @GetMapping("/lich-su/{idNguoiDung}")
    public ResponseEntity<?> getLichSuDatVe(@PathVariable("idNguoiDung") Integer idNguoiDung) {
        List<HoaDon> listHoaDon = hoaDonRepository.findByNguoiDung_IdNguoiDungOrderByNgayThanhToanDesc(idNguoiDung);

        // Chuyển đổi dữ liệu để Frontend dễ đọc
        List<Map<String, Object>> result = listHoaDon.stream().map(hd -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idHoaDon", hd.getIdHoaDon());
            map.put("ngayThanhToan", hd.getNgayThanhToan());
            map.put("trangThai", hd.getTrangThai());
            int tongTienCalculated = hd.getVes().stream()
                    .mapToInt(ve -> ve.getSuatChieu().getGiaMoiVe()) // Lấy giá từ SuatChieu
                    .sum();

            map.put("tongTien", tongTienCalculated);
            // Rút trích thông tin từ danh sách Vé bên trong Hóa đơn
            if (hd.getVes() != null && !hd.getVes().isEmpty()) {
                // Lấy thông tin phim và rạp từ cái vé đầu tiên
                Ve veDauTien = hd.getVes().get(0);
                map.put("tenPhim", veDauTien.getSuatChieu().getPhim().getTen());
                map.put("tenRap", veDauTien.getSuatChieu().getPhongChieu().getRap().getTenRap());
                map.put("thoiGianBatDau", veDauTien.getSuatChieu().getThoiGianBatDau());

                // Ghép danh sách tên ghế (VD: A1, A2)
                String danhSachGhe = hd.getVes().stream()
                        .map(v -> {
                            char tenHang = (char) ('A' + v.getGhe().getHang() - 1);
                            return tenHang + "" + v.getGhe().getCot();
                        })
                        .collect(Collectors.joining(", "));
                map.put("danhSachGhe", danhSachGhe);
            }
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
package com.cinema.cinema.service;

import com.cinema.cinema.dto.response.DoanhThuNgayDTO;
import com.cinema.cinema.dto.response.DoanhThuPhimDTO;
import com.cinema.cinema.repository.HoaDonRepository;
import com.cinema.cinema.repository.NguoiDungRepository;
import com.cinema.cinema.repository.PhimRepository;
import com.cinema.cinema.repository.VeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThongKeService {

    @Autowired
    private VeRepository veRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private PhimRepository phimRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    // 1. Lấy doanh thu phim
    public List<DoanhThuPhimDTO> layDoanhThuPhim(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.atTime(LocalTime.MAX);
        return veRepository.thongKeDoanhThuTheoPhim(start, end);
    }

    // 2. Lấy doanh thu ngày
    public List<DoanhThuNgayDTO> layDoanhThuNgay(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime start = tuNgay.atStartOfDay();
        LocalDateTime end = denNgay.atTime(LocalTime.MAX);
        return veRepository.thongKeDoanhThuTheoNgay(start, end);
    }

    public Map<String, Object> getThongKeTongQuan() {
        Map<String, Object> response = new HashMap<>();

        // 1. Các chỉ số tổng quát
        Integer doanhThu = hoaDonRepository.sumTotalRevenue();
        response.put("doanhThu", doanhThu != null ? doanhThu : 0.0);
        response.put("veDaBan", veRepository.countVeDaBan());
        response.put("soPhim", phimRepository.count());
        response.put("soNguoiDung", nguoiDungRepository.count());

        // 2. Doanh thu theo từng chi nhánh (Dành cho biểu đồ)
        List<Object[]> rawRevenueData = hoaDonRepository.sumRevenueByBranch();
        List<Map<String, Object>> doanhThuChiNhanh = rawRevenueData.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("maCoSo", row[0]);
            map.put("doanhThu", row[1]);
            return map;
        }).collect(Collectors.toList());
        response.put("doanhThuChiNhanh", doanhThuChiNhanh);

        // 3. Hoạt động gần đây
        response.put("hoatDongGanDay", hoaDonRepository.findTop5RecentWithTotal());
        return response;
    }
}

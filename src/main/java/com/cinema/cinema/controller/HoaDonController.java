package com.cinema.cinema.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinema.cinema.dto.response.ResChiTietHoaDonDTO;
import com.cinema.cinema.dto.response.ResHoaDonDTO;
import com.cinema.cinema.dto.response.RestResponse;
import com.cinema.cinema.dto.response.ResultPaginationDTO;
import com.cinema.cinema.entity.HoaDon;
import com.cinema.cinema.entity.Ve;
import com.cinema.cinema.exception.IdInvalidException;
import com.cinema.cinema.service.HoaDonService;
import com.cinema.cinema.util.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("/api/payments")

public class HoaDonController {
    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/lich-su/{idNguoiDung}")
    public ResponseEntity<?> getLichSuDatVe(@PathVariable("idNguoiDung") Integer idNguoiDung) {
        List<HoaDon> listHoaDon = hoaDonService.getLichSu(idNguoiDung);

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

    @GetMapping("")
    @PreAuthorize("hasAnyRole('QUANLY', 'NHANVIEN')")
    public ResponseEntity<ResultPaginationDTO> getAllInvoices(
            @Filter Specification<HoaDon> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.hoaDonService.fetchAllHoaDon(spec, pageable));
    }

    // 2. Cập nhật trạng thái hóa đơn
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('QUANLY', 'NHANVIEN')")
    public ResponseEntity<ResHoaDonDTO> updateInvoiceStatus(
            @PathVariable Integer id,
            @RequestParam String status) throws IdInvalidException {
        HoaDon hoaDon = this.hoaDonService.findById(id);
        if (hoaDon == null) {
            throw new IdInvalidException("Hoa don khong ton tai");
        }
        return ResponseEntity.ok(this.hoaDonService.convertToResHoaDonDTO(this.hoaDonService.updateStatus(id, status)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('QUANLY', 'NHANVIEN')")
    @ApiMessage("lấy chi tiết hóa đơn")
    public ResponseEntity<ResChiTietHoaDonDTO> getInvoiceDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(this.hoaDonService.handleGetChiTietHoaDon(id));
    }
}
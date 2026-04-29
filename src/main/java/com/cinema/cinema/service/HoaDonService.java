package com.cinema.cinema.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cinema.cinema.dto.response.ResChiTietHoaDonDTO;
import com.cinema.cinema.dto.response.ResHoaDonDTO;

import com.cinema.cinema.dto.response.ResultPaginationDTO;
import com.cinema.cinema.entity.HoaDon;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.repository.HoaDonRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;

    public HoaDonService(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    public List<HoaDon> getLichSu(int id) {
        return this.hoaDonRepository.findByNguoiDung_IdNguoiDungOrderByNgayThanhToanDesc(id);
    }

    public ResultPaginationDTO fetchAllHoaDon(Specification spec, Pageable pageable) {
        Page<HoaDon> pageHoaDon = this.hoaDonRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageHoaDon.getTotalPages());
        mt.setTotal(pageHoaDon.getTotalElements());

        rs.setMeta(mt);
        List<ResHoaDonDTO> list = pageHoaDon.getContent()
                .stream().map(item -> this.convertToResHoaDonDTO(item))
                .collect(Collectors.toList());

        rs.setResult(list);

        return rs;
    }

    public ResHoaDonDTO convertToResHoaDonDTO(HoaDon hoaDon) {
        ResHoaDonDTO res = new ResHoaDonDTO();
        res.setEmail(hoaDon.getNguoiDung().getEmail());
        res.setIdHoaDon(hoaDon.getIdHoaDon());
        res.setNgayThanhToan(hoaDon.getNgayThanhToan());
        res.setPhuongThucThanhToan(hoaDon.getPhuongThucThanhToan().getNoiDung());
        res.setTrangThai(hoaDon.getTrangThai());
        int tongTienCalculated = hoaDon.getVes().stream()
                .mapToInt(ve -> ve.getSuatChieu().getGiaMoiVe()) // Lấy giá từ SuatChieu
                .sum();
        res.setTongTien(tongTienCalculated);
        return res;
    }

    public HoaDon findById(int id) {
        Optional<HoaDon> optional = this.hoaDonRepository.findById(id);
        return optional.isPresent() ? optional.get() : null;
    }

    public HoaDon updateStatus(int id, String status) {
        HoaDon hoaDonDB = this.findById(id);
        hoaDonDB.setTrangThai(status);
        return this.hoaDonRepository.save(hoaDonDB);
    }

    public ResChiTietHoaDonDTO handleGetChiTietHoaDon(Integer id) {
        HoaDon hoaDon = this.hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        ResChiTietHoaDonDTO dto = new ResChiTietHoaDonDTO();
        dto.setIdHoaDon(hoaDon.getIdHoaDon());
        dto.setTenKhachHang(hoaDon.getNguoiDung() != null ? hoaDon.getNguoiDung().getHoTen() : "Khách vãng lai");
        dto.setSdtKhachHang(hoaDon.getNguoiDung().getSdt());
        int tongTienCalculated = hoaDon.getVes().stream()
                .mapToInt(ve -> ve.getSuatChieu().getGiaMoiVe()) // Lấy giá từ SuatChieu
                .sum();
        dto.setTongTien(tongTienCalculated);
        dto.setTrangThai(hoaDon.getTrangThai());
        List<String> dsGhe = hoaDon.getVes().stream().map(item -> item.getGhe().getTen())
                .collect(Collectors.toList());
        dto.setDanhSachGhe(dsGhe);
        dto.setGioChieu(hoaDon.getVes().get(0).getSuatChieu().getThoiGianBatDau());
        dto.setPhongChieu(hoaDon.getVes().get(0).getSuatChieu().getPhongChieu().getIdPhongChieu() + "");
        dto.setRapChieu(hoaDon.getVes().get(0).getSuatChieu().getPhongChieu().getRap().getTenRap());
        dto.setTenPhim(hoaDon.getVes().get(0).getSuatChieu().getPhim().getTen());
        dto.setPhuongThucThanhToan(hoaDon.getPhuongThucThanhToan().getNoiDung());
        return dto;
    }
}

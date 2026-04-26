package com.cinema.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cinema.cinema.entity.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer>, JpaSpecificationExecutor<HoaDon> {
    List<HoaDon> findByNguoiDung_IdNguoiDungOrderByNgayThanhToanDesc(Integer idNguoiDung);
}

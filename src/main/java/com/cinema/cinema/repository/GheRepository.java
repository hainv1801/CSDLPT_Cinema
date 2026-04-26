package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.cinema.entity.Ghe;
import com.cinema.cinema.entity.PhongChieu;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GheRepository extends JpaRepository<Ghe, Integer> {

    // Lấy danh sách ghế của 1 phòng, sắp xếp thứ tự chuẩn từ hàng 1 -> N, cột 1 ->
    // N
    @Query("SELECT g FROM Ghe g WHERE g.phongChieu.idPhongChieu = :idPhong ORDER BY g.hang ASC, g.cot ASC")
    List<Ghe> findGheByPhongChieu(@Param("idPhong") Integer idPhong);

    List<Ghe> findByPhongChieu(PhongChieu phongChieu);
}

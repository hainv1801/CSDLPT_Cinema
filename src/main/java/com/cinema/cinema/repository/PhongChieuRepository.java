package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cinema.cinema.entity.PhongChieu;

import java.util.List;

@Repository
public interface PhongChieuRepository extends JpaRepository<PhongChieu, Integer> {

    // Tìm danh sách phòng thuộc về 1 Rạp cụ thể
    @Query("SELECT p FROM PhongChieu p WHERE p.rap.id_Rap = :idRap")
    List<PhongChieu> findByRapId(@Param("idRap") Integer idRap);
}
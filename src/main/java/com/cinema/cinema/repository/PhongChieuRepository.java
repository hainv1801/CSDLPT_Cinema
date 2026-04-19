package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cinema.cinema.entity.PhongChieu;

@Repository
public interface PhongChieuRepository extends JpaRepository<PhongChieu, Integer> {

}
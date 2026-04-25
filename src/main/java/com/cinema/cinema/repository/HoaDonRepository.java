package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cinema.cinema.entity.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, String>, JpaSpecificationExecutor<HoaDon> {

}

package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cinema.cinema.entity.TheLoai;

@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Integer> {

}

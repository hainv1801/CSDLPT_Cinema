package com.cinema.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cinema.cinema.entity.Rap;

@Repository
public interface RapRepository extends JpaRepository<Rap, Integer> {

}

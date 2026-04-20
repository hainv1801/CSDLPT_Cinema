package com.cinema.cinema.controller;

import com.cinema.cinema.service.SuatChieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/suat-chieu")
public class PublicSuatChieuController {
    @Autowired
    private SuatChieuService suatChieuService;

    @GetMapping("/phim/{phimId}")
    public ResponseEntity<?> getByPhim(@PathVariable Integer phimId) {
        return ResponseEntity.ok(suatChieuService.getLichChoKhach(phimId));
    }
}

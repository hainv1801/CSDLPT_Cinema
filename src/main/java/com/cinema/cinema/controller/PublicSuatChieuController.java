package com.cinema.cinema.controller;

import com.cinema.cinema.service.SuatChieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/public/suat-chieu")
public class PublicSuatChieuController {
    @Autowired
    private SuatChieuService suatChieuService;

    @GetMapping("/public/by-phim/{idPhim}")
    public ResponseEntity<?> getByPhim(@PathVariable Integer idPhim, @RequestParam LocalDate ngay) {
        return ResponseEntity.ok(suatChieuService.getLichTheoPhim(idPhim, ngay));
    }

    // Luồng 2: Lấy lịch theo Rạp
    @GetMapping("/public/by-rap/{idRap}")
    public ResponseEntity<?> getByRap(@PathVariable Integer idRap, @RequestParam LocalDate ngay) {
        return ResponseEntity.ok(suatChieuService.getLichTheoRap(idRap, ngay));
    }

}

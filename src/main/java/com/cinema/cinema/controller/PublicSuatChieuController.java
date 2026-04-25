package com.cinema.cinema.controller;

import com.cinema.cinema.dto.response.ResSuatChieuDTO;
import com.cinema.cinema.service.SuatChieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public/suat-chieu")
public class PublicSuatChieuController {
    @Autowired
    private SuatChieuService suatChieuService;

    //Lấy lịch theo Phim
    @GetMapping("/phim/{idPhim}")
    public ResponseEntity<List<Map<String, Object>>> getLichTheoPhim(
            @PathVariable Integer idPhim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {

        LocalDate targetDate = (ngay != null) ? ngay : LocalDate.now();
        return ResponseEntity.ok(suatChieuService.getLichTheoPhim(idPhim, targetDate));
    }

    //Lấy lịch theo Rạp
    @GetMapping("/rap/{idRap}")
    public ResponseEntity<List<Map<String, Object>>> getLichTheoRap(
            @PathVariable Integer idRap,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {

        LocalDate targetDate = (ngay != null) ? ngay : LocalDate.now();
        return ResponseEntity.ok(suatChieuService.getLichTheoRap(idRap, targetDate));
    }

    //Tìm kiếm và lọc động
    @GetMapping("/search")
    public ResponseEntity<List<ResSuatChieuDTO>> searchSuatChieu(
            @RequestParam(required = false) Integer idPhim,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
            @RequestParam(required = false) String khuVuc) {

        return ResponseEntity.ok(suatChieuService.searchSuatChieu(idPhim, ngay, khuVuc));
    }
}

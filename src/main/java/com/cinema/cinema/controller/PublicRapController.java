package com.cinema.cinema.controller;

import com.cinema.cinema.dto.request.RapDTO;
import com.cinema.cinema.dto.response.ResKhuVucRapDTO;
import com.cinema.cinema.service.RapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/rap")
@CrossOrigin("*")
public class PublicRapController {

    @Autowired
    private RapService rapService;

    @GetMapping
    public ResponseEntity<List<ResKhuVucRapDTO>> getHeThongRap() {
        return ResponseEntity.ok(rapService.getHeThongRapGomNhom());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapDTO> getChiTietRap(@PathVariable Integer id) {
        return ResponseEntity.ok(rapService.getRapById(id));
    }
}

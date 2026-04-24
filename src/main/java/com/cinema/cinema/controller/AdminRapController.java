package com.cinema.cinema.controller;

import com.cinema.cinema.dto.request.RapDTO;
import com.cinema.cinema.service.RapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/raps")
@CrossOrigin("*")
public class AdminRapController {

    @Autowired
    private RapService rapService;

    //GET: /admin/raps
    @GetMapping
    public ResponseEntity<List<RapDTO>> getAll() {
        return ResponseEntity.ok(rapService.getAllRaps());
    }

    //GET: /admin/raps/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RapDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(rapService.getRapById(id));
    }

    //POST: /admin/raps
    @PostMapping
    public ResponseEntity<RapDTO> create(@RequestBody RapDTO rapDTO) {
        return new ResponseEntity<>(rapService.createRap(rapDTO), HttpStatus.CREATED);
    }

    //PUT: /admin/raps/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RapDTO> update(@PathVariable Integer id, @RequestBody RapDTO rapDTO) {
        return ResponseEntity.ok(rapService.updateRap(id, rapDTO));
    }

    // DELETE: /api/admin/raps/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        rapService.deleteRap(id);
        return ResponseEntity.ok("Đã xóa rạp thành công!");
    }
}

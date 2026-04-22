package com.cinema.cinema.controller;

import com.cinema.cinema.dto.request.ReqPhongChieuDTO;
import com.cinema.cinema.entity.Ghe;
import com.cinema.cinema.entity.PhongChieu;
import com.cinema.cinema.repository.GheRepository;
import com.cinema.cinema.repository.PhongChieuRepository;
import com.cinema.cinema.service.PhongChieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/phong-chieu")
@CrossOrigin("*")
public class AdminPhongChieuController {
    @Autowired
    private PhongChieuService phongChieuService;

    @Autowired
    private PhongChieuRepository phongChieuRepository;

    @Autowired
    private GheRepository gheRepository;

    //1. Lấy tất cả phòng chiếu
    @GetMapping
    public ResponseEntity<List<PhongChieu>> getAllPhongChieu() {
        return ResponseEntity.ok(phongChieuRepository.findAll());
    }

    //2. Lấy phòng chiếu theo rạp
    @GetMapping("/rap/{idRap}")
    public ResponseEntity<List<PhongChieu>> getPhongChieuByRap(@PathVariable Integer idRap) {
        // Lưu ý: Bạn cần tạo hàm findByRap_Id_Rap(Integer idRap) trong PhongChieuRepository nhé
        List<PhongChieu> danhSachPhong = phongChieuRepository.findByRapId(idRap);
        return ResponseEntity.ok(danhSachPhong);
    }

    //3. Tạo phòng mới và sinh ghế
    @PostMapping
    public ResponseEntity<?> createPhongChieu(@RequestBody ReqPhongChieuDTO request) {
        try {
            PhongChieu result = phongChieuService.taoPhongChieuVaSinhGhe(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tạo phòng: " + e.getMessage());
        }
    }

    //4. Lấy sơ đồ ghế
    @GetMapping("/{idPhong}/ghe")
    public ResponseEntity<List<Ghe>> getSoDoGhe(@PathVariable Integer idPhong) {
        // Query lấy ghế và sắp xếp theo Hàng (Asc) và Cột (Asc) để UI dễ vẽ lưới
        List<Ghe> danhSachGhe = gheRepository.findGheByPhongChieu(idPhong);
        return ResponseEntity.ok(danhSachGhe);
    }

    //5. Cập nhật trạng thái phòng chiếu
    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<?> updateTrangThaiPhong(@PathVariable Integer id, @RequestParam String trangThai) {
        PhongChieu phong = phongChieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu!"));

        // Chỉ cho phép đổi trạng thái, KHÔNG cho phép đổi số hàng/cột vì ghế đã sinh ra rồi
        phong.setTrangThai(trangThai);
        return ResponseEntity.ok(phongChieuRepository.save(phong));
    }

    //6. Xóa phòng chiếu
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhongChieu(@PathVariable Integer id) {
        if (!phongChieuRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Phòng chiếu không tồn tại!");
        }

        try {
            phongChieuRepository.deleteById(id);
            return ResponseEntity.ok("Xóa phòng chiếu thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể xóa phòng chiếu vì dữ liệu đang bị ràng buộc (Đã có suất chiếu/ghế). Hãy chuyển trạng thái sang BẢO TRÌ.");
        }
    }
}

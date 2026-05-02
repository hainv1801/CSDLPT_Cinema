package com.cinema.cinema.service;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cinema.cinema.dto.request.ReqDatVeDTO;
import com.cinema.cinema.entity.HoaDon;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.entity.PhuongThucThanhToan;
import com.cinema.cinema.entity.SuatChieu;
import com.cinema.cinema.repository.HoaDonRepository;
import com.cinema.cinema.repository.NguoiDungRepository;
import com.cinema.cinema.repository.PhuongThucThanhToanRepository;
import com.cinema.cinema.repository.SuatChieuRepository;

import jakarta.transaction.Transactional;

@Slf4j
@Service
public class BookingService {

    @Autowired
    private SuatChieuRepository suatChieuRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private PhuongThucThanhToanRepository phuongThucRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private VeService veService;

    @Transactional(rollbackOn = Exception.class)
    public Integer datVe(ReqDatVeDTO req) {
        log.info("--- BẮT ĐẦU QUÁ TRÌNH TẠO HÓA ĐƠN ---");

        // 1. Kiểm tra Suất chiếu, Người dùng, Phương thức thanh toán
        SuatChieu suatChieu = suatChieuRepository.findById(req.getIdSuatChieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        NguoiDung nguoiDung = nguoiDungRepository.findById(req.getIdNguoiDung())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        PhuongThucThanhToan pttt = phuongThucRepository.findById(req.getIdPhuongThucThanhToan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

        // LẤY MÃ CƠ SỞ CỦA RẠP CHIẾU (Nơi khách muốn xem phim)
        String maCoSoRap = suatChieu.getPhongChieu().getMaCoSo();

        // 2. Tạo Hóa Đơn và lưu để lấy ID
        HoaDon hoaDon = new HoaDon();
        hoaDon.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setTrangThai("DATHANHTOAN"); // Tùy logic của bạn, có thể là CHOGIAODICH
        hoaDon.setNguoiDung(nguoiDung);
        hoaDon.setPhuongThucThanhToan(pttt);
        hoaDon.setMaCoSo(maCoSoRap);

        // Lưu Hóa Đơn xuống DB nội bộ để lấy idHoaDon
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
        log.info("Đã tạo thành công Hóa Đơn ID: {} tại cơ sở: {}", savedHoaDon.getIdHoaDon(), maCoSoRap);

        // 3. Chuyển việc kiểm tra trùng ghế và tạo Vé cho Stored Procedure phân tán lo
        // Truyền thêm biến maCoSoRap vào đây để SP SQL Server biết đường định tuyến (Linked Server)
        veService.giuChoVaTaoVeTamThoi(
                savedHoaDon.getIdHoaDon(),
                req.getIdSuatChieu(),
                req.getDanhSachIdGhe(),
                maCoSoRap
        );

        // Nếu mọi thứ trót lọt (không bị văng Exception), trả về mã Hóa Đơn cho Frontend
        log.info("--- HOÀN TẤT QUÁ TRÌNH TẠO HÓA ĐƠN VÀ ĐẶT GHẾ ---");
        return savedHoaDon.getIdHoaDon();
    }
}
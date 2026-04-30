package com.cinema.cinema.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
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

@Service
public class BookingService {

        @Autowired
        private JdbcTemplate jdbcTemplate;
        // Đọc mã cơ sở từ file application.properties của Server hiện tại

        @Autowired
        private SuatChieuRepository suatChieuRepository;
        @Autowired
        private NguoiDungRepository nguoiDungRepository;
        @Autowired
        private PhuongThucThanhToanRepository phuongThucRepository;
        @Autowired
        private HoaDonRepository hoaDonRepository;

        // Inject thêm VeService
        @Autowired
        private VeService veService;

        @Transactional
        public String bookTicket(Integer suatChieuId, Integer gheId, Integer hoaDonId, Integer userId) {
                try {
                        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                                        .withProcedureName("sp_DatVePhanTan");

                        SqlParameterSource in = new MapSqlParameterSource()
                                        .addValue("id_SuatChieu", suatChieuId)
                                        .addValue("id_Ghe", gheId)
                                        .addValue("id_HoaDon", hoaDonId)
                                        .addValue("id_NguoiDung", userId);

                        jdbcCall.execute(in);
                        return "Đặt vé thành công!";
                } catch (Exception e) {
                        return "Lỗi: " + e.getMessage();
                }
        }

        @Transactional(rollbackOn = Exception.class)
        public Integer datVe(ReqDatVeDTO req) {

                // 1. Kiểm tra Suất chiếu, Người dùng, Phương thức thanh toán
                SuatChieu suatChieu = suatChieuRepository.findById(req.getIdSuatChieu())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

                NguoiDung nguoiDung = nguoiDungRepository.findById(req.getIdNguoiDung())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

                PhuongThucThanhToan pttt = phuongThucRepository.findById(req.getIdPhuongThucThanhToan())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

                // 2. Tạo Hóa Đơn và lưu để lấy ID
                HoaDon hoaDon = new HoaDon();
                hoaDon.setNgayThanhToan(LocalDateTime.now());
                hoaDon.setTrangThai("DATHANHTOAN");
                hoaDon.setNguoiDung(nguoiDung);
                hoaDon.setPhuongThucThanhToan(pttt);

                // GÁN MÃ CƠ SỞ THEO SERVER ĐANG CHẠY (Rất quan trọng cho Merge Replication)
                hoaDon.setMaCoSo(suatChieu.getPhongChieu().getMaCoSo());

                // Lưu Hóa Đơn xuống DB để lấy idHoaDon
                HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

                // 3. Chuyển việc kiểm tra trùng ghế và tạo Vé cho Stored Procedure lo
                veService.giuChoVaTaoVeTamThoi(
                                savedHoaDon.getIdHoaDon(),
                                req.getIdSuatChieu(),
                                req.getDanhSachIdGhe());

                // Nếu mọi thứ trót lọt, trả về mã Hóa Đơn cho Frontend
                return savedHoaDon.getIdHoaDon();
        }
}
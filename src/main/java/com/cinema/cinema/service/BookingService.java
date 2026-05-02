package com.cinema.cinema.service;

import java.time.LocalDateTime;
import java.util.Map; // Thêm import Map

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Thêm import Value
import org.springframework.http.HttpEntity; // Thêm import HttpEntity
import org.springframework.http.HttpHeaders; // Thêm import HttpHeaders
import org.springframework.http.MediaType; // Thêm import MediaType
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Thêm import RestTemplate

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

        // ----- CÁC CÔNG CỤ MỚI THÊM VÀO CHO KIẾN TRÚC PHÂN TÁN -----
        @Autowired
        private RestTemplate restTemplate;

        @Value("${app.cinema.ma-co-so}")
        private String maCoSoHienTai;

        @Value("${app.cinema.center-url}")
        private String centerUrl;
        // -----------------------------------------------------------

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

                // BƯỚC 1: KIỂM TRA MÁY TRẠM
                // Nếu không phải là Máy chủ Trung tâm (KV_00), thì đóng gói gửi API lên Trung
                // tâm
                if (!"KV_00".equals(maCoSoHienTai)) {
                        try {
                                String apiUrl = centerUrl + "/admin/suat-chieu/dat-ve"; // Đường dẫn API của Server
                                                                                        // Trung tâm

                                HttpHeaders headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);

                                // Đóng gói data người dùng gửi lên thành Request API
                                HttpEntity<ReqDatVeDTO> requestEntity = new HttpEntity<>(req, headers);

                                // Bắn POST request và đợi Máy chủ Trung tâm xử lý, nhận về Map chứa idHoaDon
                                Map<String, Integer> response = restTemplate.postForObject(apiUrl, requestEntity,
                                                Map.class);

                                if (response != null && response.containsKey("idHoaDon")) {
                                        return response.get("idHoaDon");
                                } else {
                                        throw new RuntimeException("Dữ liệu phản hồi từ Máy chủ không hợp lệ.");
                                }
                        } catch (Exception e) {
                                throw new RuntimeException(
                                                "Mất kết nối đến Máy chủ Trung Tâm. Không thể đặt vé lúc này: "
                                                                + e.getMessage());
                        }
                }

                // BƯỚC 2: XỬ LÝ LƯU DATABASE TẠI TRUNG TÂM
                // Đoạn code dưới đây chỉ chạy nếu Server hiện tại là KV_00 (Hoặc nếu Server
                // Trạm gửi API lên đây)

                SuatChieu suatChieu = suatChieuRepository.findById(req.getIdSuatChieu())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

                NguoiDung nguoiDung = nguoiDungRepository.findById(req.getIdNguoiDung())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

                PhuongThucThanhToan pttt = phuongThucRepository.findById(req.getIdPhuongThucThanhToan())
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

                HoaDon hoaDon = new HoaDon();
                hoaDon.setNgayThanhToan(LocalDateTime.now());
                hoaDon.setTrangThai("DATHANHTOAN");
                hoaDon.setNguoiDung(nguoiDung);
                hoaDon.setPhuongThucThanhToan(pttt);

                // Gán Mã Cơ Sở để Merge Replication biết đường ném hóa đơn này về lại đúng trạm
                hoaDon.setMaCoSo(suatChieu.getPhongChieu().getMaCoSo());

                HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

                veService.giuChoVaTaoVeTamThoi(
                                savedHoaDon.getIdHoaDon(),
                                req.getIdSuatChieu(),
                                req.getDanhSachIdGhe());

                return savedHoaDon.getIdHoaDon();
        }
}
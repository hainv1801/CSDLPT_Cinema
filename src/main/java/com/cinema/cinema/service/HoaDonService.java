package com.cinema.cinema.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.cinema.dto.request.ReqHoaDon;
import com.cinema.cinema.dto.response.ResHoaDon;
import com.cinema.cinema.entity.HoaDon;
import com.cinema.cinema.entity.NguoiDung;
import com.cinema.cinema.entity.PhuongThucThanhToan;
import com.cinema.cinema.entity.SuatChieu;
import com.cinema.cinema.repository.HoaDonRepository;
import com.cinema.cinema.repository.NguoiDungRepository;
import com.cinema.cinema.repository.PhuongThucThanhToanRepository;
import com.cinema.cinema.repository.SuatChieuRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final SuatChieuRepository suatChieuRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final PhuongThucThanhToanRepository ptttRepository;

    public HoaDonService(HoaDonRepository hoaDonRepository, SuatChieuRepository suatChieuRepository,
            NguoiDungRepository nguoiDungRepository, PhuongThucThanhToanRepository ptttRepository,
            VeService veService) {
        this.hoaDonRepository = hoaDonRepository;
        this.suatChieuRepository = suatChieuRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.ptttRepository = ptttRepository;
        this.veService = veService;
    }

    // Vẫn cần gọi Interface của Người 1
    private final VeService veService;

    @Transactional(rollbackFor = Exception.class)
    public ResHoaDon thanhToanMock(Integer idNguoiDung, ReqHoaDon request) {
        log.info("Bắt đầu thanh toán MOCK cho User [{}]", idNguoiDung);

        // 1. Kiểm tra tính hợp lệ của dữ liệu đầu vào
        NguoiDung user = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        PhuongThucThanhToan pttt = ptttRepository.findById(request.getIdPhuongThucThanhToan())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không hợp lệ"));
        SuatChieu suatChieu = suatChieuRepository.findById(request.getIdSuatChieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        // 2. Lưu Hóa đơn trực tiếp với trạng thái DATHANHTOAN
        HoaDon hoaDon = new HoaDon();
        hoaDon.setNgayThanhToan(LocalDateTime.now());
        hoaDon.setTrangThai("DATHANHTOAN"); // Bỏ qua bước CHO_THANH_TOAN
        hoaDon.setNguoiDung(user);
        hoaDon.setPhuongThucThanhToan(pttt);
        HoaDon savedHoaDon = hoaDonRepository.saveAndFlush(hoaDon);

        // 3. Gọi Người 1 kiểm tra xem ghế có ai lấy mất chưa và tạo vé (Concurrency
        // Check)
        // LƯU Ý: Nếu ghế bị cướp, hàm này sẽ văng Exception và toàn bộ Hóa Đơn này bị
        // hủy (Rollback)
        veService.giuChoVaTaoVeTamThoi(savedHoaDon.getIdHoaDon(), request.getIdSuatChieu(), request.getDanhSachIdGhe());

        // 4. Vì thanh toán thành công luôn, ta gọi chốt vé ngay lập tức
        veService.chotVe(savedHoaDon.getIdHoaDon());

        // 5. Trả kết quả về cho Frontend
        return new ResHoaDon(
                savedHoaDon.getIdHoaDon(),
                "DATHANHTOAN",
                "Thanh toán thành công! Chúc bạn xem phim vui vẻ.");
    }
}
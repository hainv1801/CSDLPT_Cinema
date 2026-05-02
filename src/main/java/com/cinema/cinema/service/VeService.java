package com.cinema.cinema.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.cinema.entity.Ve;
import com.cinema.cinema.exception.SeatAlreadyBookedException;
import com.cinema.cinema.exception.SystemTransactionException;
import com.cinema.cinema.repository.VeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j // Tự động tạo biến log để ghi chú ra console
@Service
@RequiredArgsConstructor
public class VeService {

    private final VeRepository veRepository;

//    @Transactional(rollbackFor = Exception.class)
//    public void giuChoVaTaoVeTamThoi(Integer idHoaDon, Integer idSuatChieu, List<Integer> danhSachIdGhe) {
//        log.info("BẮT ĐẦU: Đặt vé cho Hóa Đơn [{}] - Suất chiếu [{}]", idHoaDon, idSuatChieu);
//
//        if (danhSachIdGhe == null || danhSachIdGhe.isEmpty()) {
//            throw new IllegalArgumentException("Danh sách ghế không được để trống!");
//        }
//
//        for (Integer idGhe : danhSachIdGhe) {
//            // Gọi SP dưới Database để đảm bảo xử lý đồng thời an toàn
//            Integer result = veRepository.datGheAnToan(idSuatChieu, idGhe, idHoaDon);
//
//            if (result == null || result == 0) {
//                log.warn("THẤT BẠI: Ghế ID [{}] đã bị đặt mất!", idGhe);
//                // Ném lỗi để Spring Boot tự động Rollback toàn bộ Hóa Đơn và các Vé trước đó
//                throw new SeatAlreadyBookedException(
//                        "Rất tiếc, một số ghế bạn chọn vừa có người nhanh tay đặt mất. Vui lòng chọn ghế khác!");
//            } else if (result == -1) {
//                log.error("LỖI HỆ THỐNG: Database trả về -1 khi đặt ghế [{}]", idGhe);
//                throw new SystemTransactionException(
//                        "Hệ thống đang bận, không thể đặt vé lúc này. Vui lòng thử lại sau!");
//            }
//        }
//        log.info("THÀNH CÔNG: Đã tạo vé an toàn cho Hóa Đơn [{}]", idHoaDon);
//    }

    @Transactional(rollbackFor = Exception.class)
    public void giuChoVaTaoVeTamThoi(Integer idHoaDon, Integer idSuatChieu, List<Integer> danhSachIdGhe, String maCoSoRap) {
        log.info("BẮT ĐẦU: Đặt vé phân tán cho Hóa Đơn [{}] - Suất chiếu [{}] - Rạp [{}]", idHoaDon, idSuatChieu, maCoSoRap);

        if (danhSachIdGhe == null || danhSachIdGhe.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ghế không được để trống!");
        }

        // BIẾN ĐỔI: Gom danh sách ID ghế thành 1 chuỗi cách nhau bởi dấu phẩy (VD: "1,4,5")
        String chuoiGhe = danhSachIdGhe.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        try {
            // GỌI SP PHÂN TÁN: SQL Server sẽ đọc maCoSoRap và tự quyết định dùng DB nội bộ hay Linked Server
            veRepository.datVeQuaLinkedServer(idHoaDon, idSuatChieu, chuoiGhe, maCoSoRap);

        } catch (Exception e) {
            log.error("LỖI GIAO DỊCH PHÂN TÁN: {}", e.getMessage());

            // Bắt lỗi từ SP ném ra (VD: Lỗi mất kết nối Linked Server, hoặc ghế đã bị đặt)
            if (e.getMessage().contains("đã có người đặt") || e.getMessage().contains("SeatAlreadyBooked")) {
                throw new SeatAlreadyBookedException("Rất tiếc, ghế bạn chọn vừa có người đặt mất. Vui lòng chọn ghế khác!");
            } else {
                throw new SystemTransactionException("Hệ thống kết nối liên chi nhánh đang bận. Vui lòng thử lại sau!");
            }
        }

        log.info("THÀNH CÔNG: Đã tạo vé phân tán an toàn cho Hóa Đơn [{}]", idHoaDon);
    }

    @Transactional
    public void chotVe(Integer idHoaDon) {
        log.info("CHỐT VÉ: Bắt đầu chốt vé cứng cho Hóa Đơn [{}] sau khi thanh toán thành công", idHoaDon);

        List<Ve> danhSachVe = veRepository.findByHoaDon_IdHoaDon(idHoaDon);
        if (danhSachVe.isEmpty()) {
            log.error("Lỗi nghiêm trọng: Không tìm thấy vé nào cho Hóa Đơn đã thanh toán [{}]", idHoaDon);
            throw new RuntimeException("Lỗi dữ liệu: Hóa đơn đã thanh toán nhưng không có vé!");
        }

        // emailService.sendTicketConfirmation(userEmail, danhSachVe);
        log.info("CHỐT VÉ HOÀN TẤT: Đã chốt {} vé cho Hóa đơn [{}]", danhSachVe.size(), idHoaDon);
    }

    @Transactional
    public void huyGiuCho(Integer idHoaDon) {
        log.info("HỦY GIỮ CHỖ: Bắt đầu xóa các vé tạm của Hóa Đơn [{}]", idHoaDon);
        try {
            veRepository.deleteByHoaDon_IdHoaDon(idHoaDon);
            log.info("HỦY THÀNH CÔNG: Đã nhả ghế cho Hóa Đơn [{}]", idHoaDon);
        } catch (Exception e) {
            log.error("LỖI HỦY GHẾ: Không thể xóa vé của Hóa Đơn [{}]. Chi tiết: {}", idHoaDon, e.getMessage());
            throw new RuntimeException("Lỗi hệ thống khi hủy giữ chỗ");
        }
    }

    public List<Integer> layDanhSachVeTheoHoaDon(Integer idHoaDon) {
        List<Ve> veList = veRepository.findByHoaDon_IdHoaDon(idHoaDon);
        // Biến đổi List<Ve> thành List<Integer> (chỉ lấy ID vé)
        return veList.stream()
                .map(Ve::getIdVeXemPhim)
                .collect(Collectors.toList());
    }
}

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

    @Transactional(rollbackFor = Exception.class)
    // ^ Cực kỳ quan trọng: Rollback lại toàn bộ nếu có BẤT KỲ lỗi gì xảy ra
    public void giuChoVaTaoVeTamThoi(String idHoaDon, Integer idSuatChieu, List<Integer> danhSachIdGhe) {
        log.info("BẮT ĐẦU: Giữ chỗ cho Hóa Đơn [{}] - Suất chiếu [{}] - Ghế {}", idHoaDon, idSuatChieu, danhSachIdGhe);

        if (danhSachIdGhe == null || danhSachIdGhe.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ghế không được để trống!");
        }

        for (Integer idGhe : danhSachIdGhe) {
            // Gọi Stored Procedure dưới SQL Server để xử lý đồng thời (Tránh 2 người mua
            // cùng 1 ghế)
            log.debug("Đang gọi SP đặt ghế an toàn: idGhe={}", idGhe);
            Integer result = veRepository.datGheAnToan(idSuatChieu, idGhe, idHoaDon);

            if (result == 0) {
                // Ghế đã bị người khác mua mất trong tích tắc
                log.warn("THẤT BẠI: Ghế ID [{}] đã bị đặt mất!", idGhe);
                // Ném Exception sẽ làm @Transactional kích hoạt, tự động Rollback các ghế đã
                // đặt trước đó trong vòng lặp này
                throw new SeatAlreadyBookedException(
                        "Rất tiếc, ghế số " + idGhe + " vừa có người nhanh tay đặt mất. Vui lòng chọn ghế khác!");
            } else if (result == -1) {
                // Lỗi từ phía Database (Deadlock, sập mạng...)
                log.error("LỖI HỆ THỐNG: Database trả về -1 khi đặt ghế [{}]", idGhe);
                throw new SystemTransactionException(
                        "Hệ thống đang quá tải, không thể đặt vé lúc này. Vui lòng thử lại sau!");
            }
        }

        log.info("THÀNH CÔNG: Đã giữ chỗ toàn bộ ghế cho Hóa Đơn [{}]", idHoaDon);
    }

    @Transactional
    public void chotVe(String idHoaDon) {
        log.info("CHỐT VÉ: Bắt đầu chốt vé cứng cho Hóa Đơn [{}] sau khi thanh toán thành công", idHoaDon);

        // Theo thiết kế DB, trạng thái vé khi Insert là 'CHUADUNG'
        // Khi thanh toán xong, trạng thái vẫn là 'CHUADUNG' (Chờ ra rạp xem)
        // Nên ở hàm này, về mặt Database không cần Update gì thêm bảng Ve.

        // Mở rộng điểm cộng đồ án: Sinh mã QR hoặc gửi Email ở đây
        List<Ve> danhSachVe = veRepository.findByHoaDon_IdHoaDon(idHoaDon);
        if (danhSachVe.isEmpty()) {
            log.error("Lỗi nghiêm trọng: Không tìm thấy vé nào cho Hóa Đơn đã thanh toán [{}]", idHoaDon);
            throw new RuntimeException("Lỗi dữ liệu: Hóa đơn đã thanh toán nhưng không có vé!");
        }

        // emailService.sendTicketConfirmation(userEmail, danhSachVe);
        log.info("CHỐT VÉ HOÀN TẤT: Đã chốt {} vé cho Hóa đơn [{}]", danhSachVe.size(), idHoaDon);
    }

    @Transactional
    public void huyGiuCho(String idHoaDon) {
        log.info("HỦY GIỮ CHỖ: Bắt đầu xóa các vé tạm của Hóa Đơn [{}]", idHoaDon);
        try {
            veRepository.deleteByHoaDon_IdHoaDon(idHoaDon);
            log.info("HỦY THÀNH CÔNG: Đã nhả ghế cho Hóa Đơn [{}]", idHoaDon);
        } catch (Exception e) {
            log.error("LỖI HỦY GHẾ: Không thể xóa vé của Hóa Đơn [{}]. Chi tiết: {}", idHoaDon, e.getMessage());
            throw new RuntimeException("Lỗi hệ thống khi hủy giữ chỗ");
        }
    }

    public List<Integer> layDanhSachVeTheoHoaDon(String idHoaDon) {
        List<Ve> veList = veRepository.findByHoaDon_IdHoaDon(idHoaDon);
        // Biến đổi List<Ve> thành List<Integer> (chỉ lấy ID vé)
        return veList.stream()
                .map(Ve::getIdVeXemPhim)
                .collect(Collectors.toList());
    }
}

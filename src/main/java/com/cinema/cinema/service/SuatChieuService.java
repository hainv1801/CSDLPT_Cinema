package com.cinema.cinema.service;

import com.cinema.cinema.dto.request.ReqSuatChieuDTO;
import com.cinema.cinema.dto.response.ResSuatChieuDTO;
import com.cinema.cinema.entity.Phim;
import com.cinema.cinema.entity.PhongChieu;
import com.cinema.cinema.entity.Rap;
import com.cinema.cinema.entity.SuatChieu;
import com.cinema.cinema.repository.PhimRepository;
import com.cinema.cinema.repository.PhongChieuRepository;
import com.cinema.cinema.repository.SuatChieuRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuatChieuService {

    @Autowired private SuatChieuRepository repository;
    @Autowired private PhimRepository phimRepository;
    @Autowired private PhongChieuRepository phongRepository;

    @Transactional
    public ResSuatChieuDTO createSuatChieu(ReqSuatChieuDTO req) {
        Phim phim = phimRepository.findById(req.getIdPhim()).orElseThrow();
        PhongChieu phong = phongRepository.findById(req.getIdPhongChieu()).orElseThrow();

        //Tính thời gian kết thúc = Bắt đầu + Thời lượng phim + 15p dọn phòng
        LocalDateTime thoiGianKetThuc = req.getThoiGianBatDau().plusMinutes(phim.getThoiLuong().longValue() + 15);

        // Kiểm tra xem phòng đó đã có phim nào chiếu chưa
        List<SuatChieu> trungLich = repository.findOverlappingShowtimes(req.getIdPhongChieu(), req.getThoiGianBatDau(), thoiGianKetThuc);

        if (!trungLich.isEmpty()) {
            throw new RuntimeException("Phòng chiếu đã có lịch trong khoảng thời gian này!");
        }

        SuatChieu sc = new SuatChieu();
        sc.setGiaMoiVe(req.getGiaMoiVe());
        sc.setThoiGianBatDau(req.getThoiGianBatDau());
        sc.setPhim(phim);
        sc.setPhongChieu(phong);

        return mapToResponse(repository.save(sc));
    }

    //Lấy lịch chiếu
    public List<ResSuatChieuDTO> getLichChoKhach(Integer phimId) {
        return repository.findByPhimIdAndThoiGianBatDauAfter(phimId, LocalDateTime.now())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private ResSuatChieuDTO mapToResponse(SuatChieu sc) {
        ResSuatChieuDTO res = new ResSuatChieuDTO();
        BeanUtils.copyProperties(sc, res);
        res.setTenPhim(sc.getPhim().getTen());
        res.setIdPhong(sc.getPhongChieu().getIdPhongChieu());
        res.setTenRap(sc.getPhongChieu().getRap().getTenRap());
        return res;
    }

    //Lấy tất cả suất chếu
    public List<ResSuatChieuDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //Lấy 1 suất chiếu
    public ResSuatChieuDTO getById(Integer id) {
        SuatChieu suatChieu = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu!"));
        return mapToResponse(suatChieu);
    }

    //Cập nhật suất chiếu
    @Transactional
    public ResSuatChieuDTO updateSuatChieu(Integer id, ReqSuatChieuDTO req) {
        // Lấy suất chiếu hiện tại lên
        SuatChieu scHienTai = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu với ID: " + id));

        Phim phim = phimRepository.findById(req.getIdPhim())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phim"));
        PhongChieu phong = phongRepository.findById(req.getIdPhongChieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phòng chiếu"));

        // Tính thời gian kết thúc dự kiến mới
        LocalDateTime thoiGianKetThucMoi = req.getThoiGianBatDau().plusMinutes(phim.getThoiLuong().longValue() + 15);

        // Kiểm tra trùng lịch
        List<SuatChieu> trungLich = repository.findOverlappingShowtimes(req.getIdPhongChieu(), req.getThoiGianBatDau(), thoiGianKetThucMoi);

        // BƯỚC QUAN TRỌNG: Loại bỏ chính suất chiếu đang được sửa ra khỏi danh sách báo lỗi
        trungLich.removeIf(sc -> sc.getIdSuatChieu().equals(id));

        if (!trungLich.isEmpty()) {
            throw new RuntimeException("Thời gian cập nhật bị trùng với suất chiếu khác trong phòng này!");
        }

        // Cập nhật thông tin mới
        scHienTai.setGiaMoiVe(req.getGiaMoiVe());
        scHienTai.setThoiGianBatDau(req.getThoiGianBatDau());
        scHienTai.setPhim(phim);
        scHienTai.setPhongChieu(phong);

        return mapToResponse(repository.save(scHienTai));
    }

    //Xóa suất chiếu
    @Transactional
    public void deleteSuatChieu(Integer id) {
        // Kiểm tra xem có tồn tại không trước khi xóa
        if (!repository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy suất chiếu để xóa!");
        }
        repository.deleteById(id);
    }

    //Chọn phim -> Hiển thị các rạp đang chiếu
    public List<Map<String, Object>> getLichTheoPhim(Integer phimId, LocalDate ngay) {
        LocalDateTime now = (ngay.isEqual(LocalDate.now())) ? LocalDateTime.now() : ngay.atStartOfDay();
        List<SuatChieu> list = repository.findByPhimAndNgay(phimId, ngay, now);

        // Nhóm theo đối tượng Rap
        Map<Rap, List<SuatChieu>> grouped = list.stream().collect(Collectors.groupingBy(s -> s.getPhongChieu().getRap()));

        return grouped.entrySet().stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idRap", e.getKey().getIdRap());
            map.put("tenRap", e.getKey().getTenRap());
            map.put("danhSachSuat", buildSuatChieuNode(e.getValue()));
            return map;
        }).collect(Collectors.toList());
    }

    //Chọn rạp -> Hiển thị các phim đang chiếu
    public List<Map<String, Object>> getLichTheoRap(Integer rapId, LocalDate ngay) {
        LocalDateTime now = (ngay.isEqual(LocalDate.now())) ? LocalDateTime.now() : ngay.atStartOfDay();
        List<SuatChieu> list = repository.findByRapAndNgay(rapId, ngay, now);

        // Nhóm theo đối tượng Phim
        Map<Phim, List<SuatChieu>> grouped = list.stream().collect(Collectors.groupingBy(SuatChieu::getPhim));

        return grouped.entrySet().stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idPhim", e.getKey().getIdPhim());
            map.put("tenPhim", e.getKey().getTen());
            map.put("thoiLuong", e.getKey().getThoiLuong());
            map.put("danhSachSuat", buildSuatChieuNode(e.getValue()));
            return map;
        }).collect(Collectors.toList());
    }

    //Đóng gói danh sách giờ chiếu
    private List<Map<String, Object>> buildSuatChieuNode(List<SuatChieu> list) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        return list.stream().map(s -> {
            Map<String, Object> node = new HashMap<>();
            node.put("idSuat", s.getIdSuatChieu());
            node.put("batDau", s.getThoiGianBatDau().format(dtf));
            node.put("gia", s.getGiaMoiVe());
            return node;
        }).collect(Collectors.toList());
    }
}

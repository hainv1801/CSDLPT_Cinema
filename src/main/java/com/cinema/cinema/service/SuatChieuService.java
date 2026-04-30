package com.cinema.cinema.service;

import com.cinema.cinema.dto.request.ReqSuatChieuDTO;
import com.cinema.cinema.dto.response.ResGheDTO;
import com.cinema.cinema.dto.response.ResSuatChieuDTO;
import com.cinema.cinema.entity.Ghe;

import com.cinema.cinema.entity.Phim;
import com.cinema.cinema.entity.PhongChieu;
import com.cinema.cinema.entity.Rap;
import com.cinema.cinema.entity.SuatChieu;
import com.cinema.cinema.entity.Ve;
import com.cinema.cinema.repository.GheRepository;

import com.cinema.cinema.repository.PhimRepository;
import com.cinema.cinema.repository.PhongChieuRepository;
import com.cinema.cinema.repository.SuatChieuRepository;
import com.cinema.cinema.repository.VeRepository;
import com.cinema.cinema.specification.SuatChieuSpecification;

import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuatChieuService {
    @Autowired
    private PhimRepository phimRepository;
    @Autowired
    private VeRepository veRepository;
    @Autowired
    private SuatChieuRepository suatChieuRepository;
    @Autowired
    private PhongChieuRepository phongRepository;
    @Autowired
    private GheRepository gheRepository;

    @Transactional
    public ResSuatChieuDTO createSuatChieu(ReqSuatChieuDTO req) {
        Phim phim = phimRepository.findById(req.getIdPhim()).orElseThrow();
        PhongChieu phong = phongRepository.findById(req.getIdPhongChieu()).orElseThrow();

        // Tính thời gian kết thúc = Bắt đầu + Thời lượng phim + 15p dọn phòng
        LocalDateTime thoiGianKetThuc = req.getThoiGianBatDau().plusMinutes(phim.getThoiLuong().longValue() + 15);

        // Kiểm tra xem phòng đó đã có phim nào chiếu chưa
        List<SuatChieu> trungLich = suatChieuRepository.findOverlappingShowtimes(req.getIdPhongChieu(),
                req.getThoiGianBatDau(),
                thoiGianKetThuc);

        if (!trungLich.isEmpty()) {
            throw new RuntimeException("Phòng chiếu đã có lịch trong khoảng thời gian này!");
        }

        SuatChieu sc = new SuatChieu();
        sc.setGiaMoiVe(req.getGiaMoiVe());
        sc.setThoiGianBatDau(req.getThoiGianBatDau());
        sc.setPhim(phim);
        sc.setPhongChieu(phong);
        return mapToResponse(suatChieuRepository.save(sc));
    }

    // Lấy lịch chiếu
    public List<ResSuatChieuDTO> getLichChoKhach(Integer phimId) {
        return suatChieuRepository.findByPhim_IdPhimAndThoiGianBatDauAfter(phimId, LocalDateTime.now())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private ResSuatChieuDTO mapToResponse(SuatChieu sc) {
        ResSuatChieuDTO res = new ResSuatChieuDTO();
        BeanUtils.copyProperties(sc, res);
        res.setId(sc.getIdSuatChieu());
        res.setIdPhim(sc.getPhim().getIdPhim());
        res.setTenPhim(sc.getPhim().getTen());
        res.setIdPhongChieu(sc.getPhongChieu().getIdPhongChieu());
        res.setTenRap(sc.getPhongChieu().getRap().getTenRap());
        res.setIdRap(sc.getPhongChieu().getRap().getIdRap());
        return res;
    }

    // Lấy tất cả suất chiếu
    public List<ResSuatChieuDTO> getAll() {
        return suatChieuRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Lấy 1 suất chiếu
    public ResSuatChieuDTO getById(Integer id) {
        SuatChieu suatChieu = suatChieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu!"));
        return mapToResponse(suatChieu);
    }

    // Cập nhật suất chiếu
    @Transactional
    public ResSuatChieuDTO updateSuatChieu(Integer id, ReqSuatChieuDTO req) {
        // Lấy suất chiếu hiện tại lên
        SuatChieu scHienTai = suatChieuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu với ID: " + id));

        Phim phim = phimRepository.findById(req.getIdPhim())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phim"));
        PhongChieu phong = phongRepository.findById(req.getIdPhongChieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phòng chiếu"));

        // Tính thời gian kết thúc dự kiến mới
        LocalDateTime thoiGianKetThucMoi = req.getThoiGianBatDau().plusMinutes(phim.getThoiLuong().longValue() + 15);

        // Kiểm tra trùng lịch
        List<SuatChieu> trungLich = suatChieuRepository.findOverlappingShowtimes(req.getIdPhongChieu(),
                req.getThoiGianBatDau(),
                thoiGianKetThucMoi);

        // BƯỚC QUAN TRỌNG: Loại bỏ chính suất chiếu đang được sửa ra khỏi danh sách báo
        // lỗi
        trungLich.removeIf(sc -> sc.getIdSuatChieu().equals(id));

        if (!trungLich.isEmpty()) {
            throw new RuntimeException("Thời gian cập nhật bị trùng với suất chiếu khác trong phòng này!");
        }

        // Cập nhật thông tin mới
        scHienTai.setGiaMoiVe(req.getGiaMoiVe());
        scHienTai.setThoiGianBatDau(req.getThoiGianBatDau());
        scHienTai.setPhim(phim);
        scHienTai.setPhongChieu(phong);

        return mapToResponse(suatChieuRepository.save(scHienTai));
    }

    // Xóa suất chiếu
    @Transactional
    public void deleteSuatChieu(Integer id) {
        // Kiểm tra xem có tồn tại không trước khi xóa
        if (!suatChieuRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy suất chiếu để xóa!");
        }
        suatChieuRepository.deleteById(id);
    }

    // Tìm kiếm suất chiếu
    public List<ResSuatChieuDTO> searchSuatChieu(Integer idPhim, LocalDate ngay, String khuVuc) {
        Specification<SuatChieu> spec = SuatChieuSpecification.filterShowtimes(idPhim, ngay, khuVuc);

        return suatChieuRepository.findAll(spec).stream()
                .map(this::mapToResponse) // Tái sử dụng hàm map đã viết ở các phần trước
                .collect(Collectors.toList());
    }

    // Chọn phim -> Hiển thị các rạp đang chiếu
    public List<Map<String, Object>> getLichTheoPhim(Integer phimId, LocalDate ngay) {
        // Thời điểm bắt đầu của ngày (00:00:00)
        LocalDateTime startOfDay = ngay.atStartOfDay();

        // Thời điểm kết thúc của ngày (23:59:59.999999999)
        LocalDateTime endOfDay = ngay.atTime(LocalTime.MAX);

        // Thời gian hiện tại
        LocalDateTime thoiGianHienTai = LocalDateTime.now();
        List<SuatChieu> list = suatChieuRepository.findByPhimAndNgay(phimId, startOfDay, endOfDay, thoiGianHienTai);

        // Nhóm theo đối tượng Rap
        Map<Rap, List<SuatChieu>> grouped = list.stream()
                .collect(Collectors.groupingBy(s -> s.getPhongChieu().getRap()));

        return grouped.entrySet().stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idRap", e.getKey().getIdRap());
            map.put("tenRap", e.getKey().getTenRap());
            map.put("danhSachSuat", buildSuatChieuNode(e.getValue()));
            return map;
        }).collect(Collectors.toList());
    }

    // Chọn rạp -> Hiển thị các phim đang chiếu
    public List<Map<String, Object>> getLichTheoRap(Integer rapId, LocalDate ngay) {
        // Thời điểm bắt đầu của ngày (00:00:00)
        LocalDateTime startOfDay = ngay.atStartOfDay();

        // Thời điểm kết thúc của ngày (23:59:59.999999999)
        LocalDateTime endOfDay = ngay.atTime(LocalTime.MAX);

        // Thời gian hiện tại
        LocalDateTime thoiGianHienTai = LocalDateTime.now();
        List<SuatChieu> list = suatChieuRepository.findByRapAndNgay(rapId, startOfDay, endOfDay, thoiGianHienTai);

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

    // Đóng gói danh sách giờ chiếu
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

    public List<ResGheDTO> layDanhSachGhe(Integer idSuatChieu) {
        // 1. Tìm suất chiếu -> Từ đó lấy ra cái Phòng chiếu -> Lấy được TOÀN BỘ GHẾ của
        // phòng đó.
        SuatChieu suat = suatChieuRepository.findById(idSuatChieu).orElseThrow();
        List<Ghe> tatCaGhe = gheRepository.findByPhongChieu(suat.getPhongChieu());

        // 2. Tìm TOÀN BỘ VÉ đã được bán ra thuộc về idSuatChieu này.
        List<Ve> veDaBan = veRepository.findBySuatChieu(suat);

        // Rút trích ra 1 danh sách chỉ chứa ID của các ghế đã bị mua
        List<Integer> listIdGheDaMua = veDaBan.stream()
                .map(ve -> ve.getGhe().getIdGhe())
                .toList();

        // 3. Lắp ráp thành DTO
        List<ResGheDTO> result = new ArrayList<>();
        for (Ghe g : tatCaGhe) {
            ResGheDTO dto = new ResGheDTO();
            dto.setIdGhe(g.getIdGhe());
            // Ép kiểu số 1 thành chữ 'A', số 2 thành chữ 'B'...
            char tenHang = (char) ('A' + g.getHang() - 1);

            // Ghép chữ cái hàng với số cột
            dto.setTenGhe(tenHang + "" + g.getCot());
            // Nếu ID ghế nằm trong mảng đã mua -> daDat = true
            if (listIdGheDaMua.contains(g.getIdGhe())) {
                dto.setDaDat(1);
            } else {
                dto.setDaDat(0);
            }
            result.add(dto);
        }

        return result;
    }
}

package com.cinema.cinema.service;

import com.cinema.cinema.dto.request.ReqPhimDTO;
import com.cinema.cinema.dto.response.ResChiTietPhimDTO;
import com.cinema.cinema.dto.response.ResPhimDTO;
import com.cinema.cinema.entity.Phim;
import com.cinema.cinema.entity.TheLoai;
import com.cinema.cinema.repository.PhimRepository;
import com.cinema.cinema.repository.TheLoaiRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhimService {

    @Autowired
    private PhimRepository phimRepository;

    @Autowired
    private TheLoaiRepository theLoaiRepository;

    // 1. Lấy tất cả phim
    public List<ResPhimDTO> getAll() {
        return phimRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // 2. Lấy 1 phim
    public ResPhimDTO getById(Integer id) {
        Phim phim = phimRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        return mapToResponse(phim);
    }

    // Lấy danh sách phim đang chiếu
    public List<ResPhimDTO> getDanhSachPhimDangChieu() {
        LocalDateTime now = LocalDateTime.now();
        List<Phim> danhSachGoc = phimRepository.findPhimDangChieu(now);
        return mapListToResponse(danhSachGoc);
    }

    // Lấy danh sách phim sắp chiếu
    public List<ResPhimDTO> getDanhSachPhimSapChieu() {
        LocalDate today = LocalDate.now();
        List<Phim> danhSachGoc = phimRepository.findPhimSapChieu(today);
        return mapListToResponse(danhSachGoc);
    }

    // Lấy chi tiết 1 bộ phim
    public ResChiTietPhimDTO getChiTietPhim(Integer idPhim) {
        // 1. Tìm phim trong Database
        Phim phim = phimRepository.findById(idPhim)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với ID: " + idPhim));

        // 2. Chuyển đổi từ Entity sang DTO
        ResChiTietPhimDTO response = new ResChiTietPhimDTO();
        BeanUtils.copyProperties(phim, response);
        response.setIdPhim(phim.getIdPhim());
        response.setThoiLuong(phim.getThoiLuong());
        response.setPoster(phim.getPoster());
        // 3. Xử lý lấy danh sách Tên Thể Loại
        if (phim.getTheLoais() != null && !phim.getTheLoais().isEmpty()) {
            response.setDanhSachTheLoai(
                    phim.getTheLoais().stream()
                            .map(TheLoai::getNoiDung)
                            .collect(Collectors.toList()));
        }

        return response;
    }

    // 3. Tạo phim mới
    @Transactional
    public ResPhimDTO create(ReqPhimDTO request) {
        Phim phim = new Phim();
        updateEntityFromRequest(phim, request);
        return mapToResponse(phimRepository.save(phim));
    }

    // 4. Cập nhật thông tin phim
    @Transactional
    public ResPhimDTO update(Integer id, ReqPhimDTO request) {
        Phim phim = phimRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
        updateEntityFromRequest(phim, request);
        return mapToResponse(phimRepository.save(phim));
    }

    // 5. Xóa phim
    public void delete(Integer id) {
        if (!phimRepository.existsById(id))
            throw new RuntimeException("Không tìm thấy phim để xóa");
        phimRepository.deleteById(id);
    }

    private void updateEntityFromRequest(Phim phim, ReqPhimDTO request) {
        phim.setTen(request.getTen());
        phim.setNgayPhatHanh(request.getNgayPhatHanh());
        phim.setThoiLuong(request.getThoiLuong());
        phim.setNgonNguChinh(request.getNgonNguChinh());
        phim.setNoiDung(request.getNoiDung());
        if (request.getPoster() != null && !request.getPoster().isEmpty()) {
            phim.setPoster(request.getPoster());
        }
        if (request.getTheLoaiIds() != null) {
            List<TheLoai> theLoais = theLoaiRepository.findAllById(request.getTheLoaiIds());
            phim.setTheLoais(new HashSet<>(theLoais));
        }
    }

    private ResPhimDTO mapToResponse(Phim phim) {
        ResPhimDTO res = new ResPhimDTO();
        BeanUtils.copyProperties(phim, res);
        res.setId(phim.getIdPhim());
        res.setTenTheLoais(phim.getTheLoais().stream().map(TheLoai::getNoiDung).collect(Collectors.toList()));
        return res;
    }

    private List<ResPhimDTO> mapListToResponse(List<Phim> listPhim) {
        return listPhim.stream().map(phim -> {
            ResPhimDTO res = new ResPhimDTO();
            BeanUtils.copyProperties(phim, res);
            res.setId(phim.getIdPhim());
            // Xử lý lấy danh sách tên Thể Loại (để UI in ra "Hành Động, Hài, Kịch Tính")
            if (phim.getTheLoais() != null && !phim.getTheLoais().isEmpty()) {
                List<String> tenTheLoais = phim.getTheLoais().stream()
                        .map(TheLoai::getNoiDung)
                        .collect(Collectors.toList());
                res.setTenTheLoais(tenTheLoais);
            }
            return res;
        }).collect(Collectors.toList());
    }
}

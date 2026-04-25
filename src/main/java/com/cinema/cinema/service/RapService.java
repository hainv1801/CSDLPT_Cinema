package com.cinema.cinema.service;

import com.cinema.cinema.dto.request.RapDTO;
import com.cinema.cinema.dto.response.ResKhuVucRapDTO;
import com.cinema.cinema.entity.Rap;
import com.cinema.cinema.repository.RapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RapService {
    @Autowired
    private RapRepository rapRepository;

    //Lấy danh sách tất cả caác rạp
    public List<RapDTO> getAllRaps() {
        return rapRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    //Lấy rạp theo ID
    public RapDTO getRapById(Integer id) {
        Rap rap = rapRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp!"));
        return mapToDTO(rap);
    }

    //Thêm rạp mới
    public RapDTO createRap(RapDTO rapDTO) {
        Rap rap = new Rap();
        rap.setTenRap(rapDTO.getTenRap());
        rap.setDiaChi(rapDTO.getDiaChi());
        rap.setKhuVuc(rapDTO.getKhuVuc());

        Rap savedRap = rapRepository.save(rap);
        return mapToDTO(savedRap);
    }

    //Cập nhật rạp
    public RapDTO updateRap(Integer id, RapDTO rapDTO) {
        Rap rap = rapRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy rạp!"));
        rap.setTenRap(rapDTO.getTenRap());
        rap.setDiaChi(rapDTO.getDiaChi());
        rap.setKhuVuc(rapDTO.getKhuVuc());

        Rap updatedRap = rapRepository.save(rap);
        return mapToDTO(updatedRap);
    }

    //Xóa rạp
    public void deleteRap(Integer id) {
        rapRepository.deleteById(id);
    }

    private RapDTO mapToDTO(Rap rap) {
        RapDTO dto = new RapDTO();
        dto.setId_Rap(rap.getIdRap());
        dto.setTenRap(rap.getTenRap());
        dto.setDiaChi(rap.getDiaChi());
        dto.setKhuVuc(rap.getKhuVuc());
        return dto;
    }

    //Lấy toàn bộ rạp đã được chia theo khu vực
    public List<ResKhuVucRapDTO> getHeThongRapGomNhom() {

        List<Rap> tatCaRap = rapRepository.findAll();

        Map<String, List<Rap>> mapGomNhom = tatCaRap.stream()
                .collect(Collectors.groupingBy(Rap::getDiaChi));

        return mapGomNhom.entrySet().stream()
                .map(entry -> new ResKhuVucRapDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}

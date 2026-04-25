package com.cinema.cinema.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cinema.cinema.entity.PhuongThucThanhToan;
import com.cinema.cinema.repository.PhuongThucThanhToanRepository;

@Service
public class PhuongThucThanhToanService {
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    public PhuongThucThanhToanService(PhuongThucThanhToanRepository phuongThucThanhToanRepository) {
        this.phuongThucThanhToanRepository = phuongThucThanhToanRepository;
    }

    public List<PhuongThucThanhToan> handleGetActivePaymentMethods() {
        return this.phuongThucThanhToanRepository.findAll();
    }
}

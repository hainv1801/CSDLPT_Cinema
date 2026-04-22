package com.cinema.cinema.specification;

import com.cinema.cinema.entity.SuatChieu;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuatChieuSpecification {

    public static Specification<SuatChieu> filterShowtimes(Integer idPhim, LocalDate ngay, String khuVuc) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo Phim
            if (idPhim != null) {
                predicates.add(cb.equal(root.get("phim").get("id"), idPhim));
            }

            // 2. Lọc theo Ngày (So sánh ngày bắt đầu bằng ngày chọn)
            if (ngay != null) {
                predicates.add(cb.equal(cb.function("DATE", LocalDate.class, root.get("thoiGianBatDau")), ngay));
            }

            // 3. Lọc theo Khu vực (Join từ Suất chiếu -> Phòng -> Rạp)
            if (khuVuc != null && !khuVuc.isEmpty()) {
                predicates.add(cb.like(root.join("phongChieu").join("rap").get("khuVuc"), "%" + khuVuc + "%"));
            }

            // Chỉ lấy các suất chiếu từ thời điểm hiện tại trở đi
            predicates.add(cb.greaterThan(root.get("thoiGianBatDau"), java.time.LocalDateTime.now()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

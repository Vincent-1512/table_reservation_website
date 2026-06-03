package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.restaurant.entity.Area;
import vn.edu.ptit.restaurant.repository.AreaRepository;
import vn.edu.ptit.restaurant.repository.DiningTableRepository;
import vn.edu.ptit.restaurant.service.AreaService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final DiningTableRepository diningTableRepository;

    @Override
    public List<Area> findAll() {
        return areaRepository.findAll();
    }

    @Override
    public Optional<Area> findById(Integer id) {
        return areaRepository.findById(id);
    }

    @Override
    @Transactional
    public Area save(Area area) {
        if (area.getName() == null || area.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên khu vực không được để trống");
        }

        area.setName(area.getName().trim());
        return areaRepository.save(area);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));

        if (diningTableRepository.existsByAreaId(id)) {
            throw new RuntimeException("Không thể xóa khu vực này vì vẫn còn bàn thuộc khu vực");
        }

        areaRepository.delete(area);
    }
}
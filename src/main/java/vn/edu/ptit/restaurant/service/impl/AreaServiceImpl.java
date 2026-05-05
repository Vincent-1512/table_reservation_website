package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.ptit.restaurant.entity.Area;
import vn.edu.ptit.restaurant.repository.AreaRepository;
import vn.edu.ptit.restaurant.service.AreaService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;

    @Override
    public List<Area> findAll() {
        return areaRepository.findAll();
    }

    @Override
    public Optional<Area> findById(Integer id) {
        return areaRepository.findById(id);
    }

    @Override
    public Area save(Area area) {
        return areaRepository.save(area);
    }

    @Override
    public void deleteById(Integer id) {
        areaRepository.deleteById(id);
    }
}

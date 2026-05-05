package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.repository.DiningTableRepository;
import vn.edu.ptit.restaurant.service.DiningTableService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl implements DiningTableService {

    private final DiningTableRepository diningTableRepository;

    @Override
    public List<DiningTable> findAll() {
        return diningTableRepository.findAll();
    }

    @Override
    public List<DiningTable> findByAreaId(Integer areaId) {
        return diningTableRepository.findByAreaId(areaId);
    }

    @Override
    public Optional<DiningTable> findById(Long id) {
        return diningTableRepository.findById(id);
    }

    @Override
    public DiningTable save(DiningTable diningTable) {
        return diningTableRepository.save(diningTable);
    }

    @Override
    public void deleteById(Long id) {
        diningTableRepository.deleteById(id);
    }
}

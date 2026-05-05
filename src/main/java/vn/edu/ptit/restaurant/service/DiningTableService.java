package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.DiningTable;
import java.util.List;
import java.util.Optional;

public interface DiningTableService {
    List<DiningTable> findAll();
    List<DiningTable> findByAreaId(Integer areaId);
    Optional<DiningTable> findById(Long id);
    DiningTable save(DiningTable diningTable);
    void deleteById(Long id);
}

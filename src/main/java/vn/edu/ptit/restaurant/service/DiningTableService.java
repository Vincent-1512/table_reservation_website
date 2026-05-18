package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.entity.DiningTable;
import java.util.List;
import java.util.Optional;

public interface DiningTableService {
    List<DiningTable> findAll();
    List<DiningTable> findByAreaId(Integer areaId);
    List<DiningTable> findByAreaIdAndStatus(Integer areaId, TableStatus status);
    List<DiningTable> findByStatus(TableStatus status);
    Optional<DiningTable> findById(Long id);
    DiningTable save(DiningTable diningTable);
    void deleteById(Long id);

}

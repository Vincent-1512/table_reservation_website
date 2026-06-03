package vn.edu.ptit.restaurant.service;

import org.springframework.data.domain.Page;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;

import java.util.List;
import java.util.Optional;

public interface DiningTableService {

    List<DiningTable> findAll();

    List<DiningTable> findByAreaId(Integer areaId);

    List<DiningTable> findByAreaIdAndStatus(Integer areaId, TableStatus status);

    List<DiningTable> findByStatus(TableStatus status);

    Page<DiningTable> searchTables(String keyword,
                                   Integer areaId,
                                   TableStatus status,
                                   Integer minCapacity,
                                   int page,
                                   int size);

    Optional<DiningTable> findById(Long id);

    DiningTable save(DiningTable diningTable);

    void deleteById(Long id);
}
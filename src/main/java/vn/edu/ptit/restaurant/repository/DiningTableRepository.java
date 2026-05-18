package vn.edu.ptit.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;

import java.util.List;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    long countByStatus(TableStatus status);
    List<DiningTable> findByAreaId(Integer areaId);
    List<DiningTable> findByAreaIdAndStatus(Integer areaId, TableStatus status);
    List<DiningTable> findByStatus(TableStatus status);
}

package vn.edu.ptit.restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    boolean existsByAreaId(Integer areaId);

    List<DiningTable> findByAreaIdAndTableNameIgnoreCase(Integer areaId, String tableName);

    @Query("""
            SELECT t
            FROM DiningTable t
            JOIN t.area a
            WHERE (:areaId IS NULL OR a.id = :areaId)
              AND (:status IS NULL OR t.status = :status)
              AND (:minCapacity IS NULL OR t.capacity >= :minCapacity)
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(t.tableName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            ORDER BY t.id DESC
            """)
    Page<DiningTable> searchTables(@Param("keyword") String keyword,
                                   @Param("areaId") Integer areaId,
                                   @Param("status") TableStatus status,
                                   @Param("minCapacity") Integer minCapacity,
                                   Pageable pageable);
}
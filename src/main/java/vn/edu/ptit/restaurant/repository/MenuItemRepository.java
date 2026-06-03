package vn.edu.ptit.restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.MenuItem;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategoryId(Integer categoryId);

    List<MenuItem> findByNameContainingIgnoreCase(String keyword);

    List<MenuItem> findByIsAvailable(Boolean isAvailable);

    boolean existsByCategoryId(Integer categoryId);

    @Query("""
            SELECT m
            FROM MenuItem m
            JOIN m.category c
            WHERE (:categoryId IS NULL OR c.id = :categoryId)
              AND (:isAvailable IS NULL OR m.isAvailable = :isAvailable)
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            ORDER BY m.id DESC
            """)
    Page<MenuItem> searchMenuItems(@Param("keyword") String keyword,
                                   @Param("categoryId") Integer categoryId,
                                   @Param("isAvailable") Boolean isAvailable,
                                   Pageable pageable);
}
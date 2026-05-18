package vn.edu.ptit.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.MenuItem;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryId(Integer categoryId);
    List<MenuItem> findByNameContainingIgnoreCase(String keyword);
    List<MenuItem> findByIsAvailable(Boolean isAvailable);  
    // List<MenuItem> findByIsAvailable(Boolean isAvailable);
}

package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.MenuItem;
import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    List<MenuItem> findAll();
    List<MenuItem> findByCategoryId(Integer categoryId);
    List<MenuItem> searchByName(String keyword);
    List<MenuItem> findByIsAvailable(Boolean isAvailable);

    Optional<MenuItem> findById(Long id);
    MenuItem save(MenuItem menuItem);
        
    void toggleAvailability(Long id);
    void deleteById(Long id);
}

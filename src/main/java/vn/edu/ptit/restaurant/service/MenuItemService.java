package vn.edu.ptit.restaurant.service;

import org.springframework.data.domain.Page;
import vn.edu.ptit.restaurant.entity.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemService {

    List<MenuItem> findAll();

    List<MenuItem> findByCategoryId(Integer categoryId);

    List<MenuItem> searchByName(String keyword);

    List<MenuItem> findByIsAvailable(Boolean isAvailable);

    Page<MenuItem> searchMenuItems(String keyword,
                                   Integer categoryId,
                                   Boolean isAvailable,
                                   int page,
                                   int size);

    Optional<MenuItem> findById(Long id);

    MenuItem save(MenuItem menuItem);

    void toggleAvailability(Long id);

    void deleteById(Long id);
}
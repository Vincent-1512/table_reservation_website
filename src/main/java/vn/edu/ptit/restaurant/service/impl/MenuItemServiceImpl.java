package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.ptit.restaurant.entity.MenuItem;
import vn.edu.ptit.restaurant.repository.MenuItemRepository;
import vn.edu.ptit.restaurant.service.MenuItemService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    @Override
    public List<MenuItem> findByCategoryId(Integer categoryId) {
        return menuItemRepository.findByCategoryId(categoryId);
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return menuItemRepository.findById(id);
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteById(Long id) {
        menuItemRepository.deleteById(id);
    }

    @Override
    public List<MenuItem> searchByName(String keyword) {
        return menuItemRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<MenuItem> findByIsAvailable(Boolean isAvailable) {
        return menuItemRepository.findByIsAvailable(isAvailable);
    }

    @Override
    public void toggleAvailability(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));

        Boolean currentStatus = item.getIsAvailable();

        if (currentStatus == null) {
            currentStatus = true;
        }

        item.setIsAvailable(!currentStatus);

        menuItemRepository.save(item);
    }
}

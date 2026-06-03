package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.restaurant.entity.Category;
import vn.edu.ptit.restaurant.repository.CategoryRepository;
import vn.edu.ptit.restaurant.repository.MenuItemRepository;
import vn.edu.ptit.restaurant.service.CategoryService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public Category save(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống");
        }

        category.setName(category.getName().trim());

        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        if (menuItemRepository.existsByCategoryId(id)) {
            throw new RuntimeException("Không thể xóa danh mục này vì vẫn còn món ăn thuộc danh mục");
        }

        categoryRepository.delete(category);
    }
}
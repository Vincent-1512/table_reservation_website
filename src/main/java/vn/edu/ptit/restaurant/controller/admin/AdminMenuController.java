package vn.edu.ptit.restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.ptit.restaurant.entity.Category;
import vn.edu.ptit.restaurant.entity.MenuItem;
import vn.edu.ptit.restaurant.service.CategoryService;
import vn.edu.ptit.restaurant.service.MenuItemService;



import java.util.List;

@Controller
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {

    private final CategoryService categoryService;
    private final MenuItemService menuItemService;

    // Hiển thị danh sách Danh mục và Món ăn
    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Integer categoryId,
                        @RequestParam(required = false) Boolean isAvailable,
                        Model model) {

        List<MenuItem> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = menuItemService.searchByName(keyword.trim());
        } else if (categoryId != null) {
            items = menuItemService.findByCategoryId(categoryId);
        } else if (isAvailable != null) {
            items = menuItemService.findByIsAvailable(isAvailable);
        } else {
            items = menuItemService.findAll();
        }

        model.addAttribute("menuItems", items);
        model.addAttribute("categories", categoryService.findAll());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedAvailability", isAvailable);

        return "admin/menu/index";
    }


    @GetMapping("/{id}/toggle-availability")
    public String toggleAvailability(@PathVariable Long id) {
        menuItemService.toggleAvailability(id);
        return "redirect:/admin/menu";
    }
    
    // ================= CATEGORY ================= //
    @GetMapping("/category/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/menu/category-form";
    }

    @PostMapping("/category/save")
    public String saveCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/admin/menu";
    }

    @GetMapping("/category/edit/{id}")
    public String showEditCategoryForm(@PathVariable Integer id, Model model) {
        categoryService.findById(id).ifPresent(c -> model.addAttribute("category", c));
        return "admin/menu/category-form";
    }

    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        categoryService.deleteById(id);
        return "redirect:/admin/menu";
    }

    // ================= MENU ITEM ================= //
    @GetMapping("/item/add")
    public String showAddItemForm(Model model) {
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/menu/item-form";
    }

    @PostMapping("/item/save")
    public String saveMenuItem(@ModelAttribute MenuItem menuItem) {
        menuItemService.save(menuItem);
        return "redirect:/admin/menu";
    }

    @GetMapping("/items/{id}/edit")
    public String showEditItemForm(@PathVariable Long id, Model model) {
        menuItemService.findById(id).ifPresent(m -> model.addAttribute("menuItem", m));
        model.addAttribute("categories", categoryService.findAll());
        return "admin/menu/item-form";
    }

    @GetMapping("/item/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteById(id);
        return "redirect:/admin/menu";
    }
}

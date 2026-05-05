package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.restaurant.entity.Category;
import vn.edu.ptit.restaurant.entity.MenuItem;
import vn.edu.ptit.restaurant.service.CategoryService;
import vn.edu.ptit.restaurant.service.MenuItemService;

@Controller
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {

    private final CategoryService categoryService;
    private final MenuItemService menuItemService;

    // Hiển thị danh sách Danh mục và Món ăn
    @GetMapping
    public String index(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("menuItems", menuItemService.findAll());
        return "admin/menu/index";
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

    @GetMapping("/item/edit/{id}")
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

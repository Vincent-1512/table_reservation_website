package vn.edu.ptit.restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.restaurant.entity.Category;
import vn.edu.ptit.restaurant.entity.MenuItem;
import vn.edu.ptit.restaurant.service.CategoryService;
import vn.edu.ptit.restaurant.service.MenuItemService;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Controller
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {

    private final CategoryService categoryService;
    private final MenuItemService menuItemService;

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Integer categoryId,
                        @RequestParam(required = false) Boolean isAvailable,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

        Page<MenuItem> menuItemPage = menuItemService.searchMenuItems(
                keyword,
                categoryId,
                isAvailable,
                page,
                size
        );

        model.addAttribute("menuItems", menuItemPage.getContent());
        model.addAttribute("categories", categoryService.findAll());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", menuItemPage.getTotalPages());
        model.addAttribute("totalItems", menuItemPage.getTotalElements());
        model.addAttribute("size", size);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedAvailability", isAvailable);

        return "admin/menu/index";
    }

    @PostMapping("/{id}/toggle-availability")
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

    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Integer id,
                                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

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
    public String saveMenuItem(@ModelAttribute MenuItem menuItem,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveMenuImage(imageFile);
                menuItem.setImageUrl(imageUrl);
            }

            menuItemService.save(menuItem);

            redirectAttributes.addFlashAttribute("success", "Lưu món ăn thành công");
            return "redirect:/admin/menu";

        } catch (RuntimeException | IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi upload ảnh: " + e.getMessage());

            if (menuItem.getId() != null) {
                return "redirect:/admin/menu/item/edit/" + menuItem.getId();
            }

            return "redirect:/admin/menu/item/add";
        }
    }

    @GetMapping("/items/{id}/edit")
    public String showEditItemForm(@PathVariable Long id, Model model) {
        menuItemService.findById(id).ifPresent(m -> model.addAttribute("menuItem", m));
        model.addAttribute("categories", categoryService.findAll());
        return "admin/menu/item-form";
    }

    @PostMapping("/item/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteById(id);
        return "redirect:/admin/menu";
    }

    private String saveMenuImage(MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new RuntimeException("Tên file ảnh không hợp lệ");
        }

        String extension = getFileExtension(originalFilename);

        if (!isAllowedImageExtension(extension)) {
            throw new RuntimeException("Chỉ cho phép upload file ảnh JPG, JPEG, PNG, WEBP");
        }

        if (imageFile.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Dung lượng ảnh không được vượt quá 5MB");
        }

        Path uploadDir = Paths.get("uploads/menu");
        Files.createDirectories(uploadDir);

        String fileName = UUID.randomUUID() + extension;
        Path targetPath = uploadDir.resolve(fileName);

        Files.copy(imageFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/menu/" + fileName;
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");

        if (dotIndex == -1) {
            return "";
        }

        return filename.substring(dotIndex).toLowerCase();
    }

    private boolean isAllowedImageExtension(String extension) {
        return extension.equals(".jpg")
                || extension.equals(".jpeg")
                || extension.equals(".png")
                || extension.equals(".webp");
    }
}
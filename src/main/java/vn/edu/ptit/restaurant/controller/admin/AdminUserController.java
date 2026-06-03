package vn.edu.ptit.restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.enums.Role;
import vn.edu.ptit.restaurant.service.UserService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) Role role,
                        @RequestParam(required = false) String keyword,
                        Model model,
                        Principal principal) {

        Page<User> userPage = userService.searchActiveUsers(keyword, role, page, size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("size", size);

        model.addAttribute("roles", Role.values());
        model.addAttribute("selectedRole", role);
        model.addAttribute("keyword", keyword);

        User currentUser = null;
        if (principal != null) {
            currentUser = userService.findByUsername(principal.getName()).orElse(null);
        }
        model.addAttribute("currentUser", currentUser);

        return "admin/user/index";
    }

    @PostMapping("/{id}/role")
    public String updateRole(@PathVariable Long id,
                             @RequestParam Role role,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateRole(id, role, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật quyền người dùng thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "admin/user/form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user,
                        RedirectAttributes redirectAttributes) {
        try {
            userService.createUserByAdmin(user);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo tài khoản thành công");
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/add";
        }
    }
}
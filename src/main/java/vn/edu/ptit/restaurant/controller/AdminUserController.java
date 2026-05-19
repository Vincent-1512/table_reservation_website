package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.enums.Role;
import vn.edu.ptit.restaurant.service.UserService;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "2") int size,
                        @RequestParam(required = false) Role role,
                        @RequestParam(required = false) String keyword,
                        Model model) {

        Page<User> userPage;

        if (keyword != null && !keyword.trim().isEmpty()) {

            List<User> users = userService.search(keyword.trim());

            model.addAttribute("users", users);

        } else if (role != null) {

            List<User> users = userService.findByRole(role);

            model.addAttribute("users", users);

        } else {

            userPage = userService.findPaginated(page, size);

            model.addAttribute("users", userPage.getContent());

            model.addAttribute("currentPage", page);

            model.addAttribute("totalPages", userPage.getTotalPages());
        }

        model.addAttribute("roles", Role.values());
        model.addAttribute("selectedRole", role);
        model.addAttribute("keyword", keyword);

        return "admin/user/index";
    }

    @PostMapping("/{id}/role")
    public String updateRole(@PathVariable Long id,
                             @RequestParam Role role) {
        userService.updateRole(id, role);
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }
}
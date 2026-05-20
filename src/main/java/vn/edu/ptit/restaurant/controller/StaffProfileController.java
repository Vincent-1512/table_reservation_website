package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/staff/profile")
@RequiredArgsConstructor
public class StaffProfileController {

    private final UserService userService;

    @GetMapping
    public String index(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        model.addAttribute("user", user);
        return "staff/profile/index";
    }

    @PostMapping("/update")
    public String updateProfile(Principal principal,
                                @RequestParam String fullName,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String email,
                                RedirectAttributes redirectAttrs) {
        try {
            userService.updateProfile(principal.getName(), fullName, phone, email);
            redirectAttrs.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(Principal principal,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttrs) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttrs.addFlashAttribute("error", "Mật khẩu mới và xác nhận không khớp!");
            return "redirect:/staff/profile";
        }
        if (newPassword.length() < 6) {
            redirectAttrs.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return "redirect:/staff/profile";
        }
        try {
            userService.changePassword(principal.getName(), oldPassword, newPassword);
            redirectAttrs.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/profile";
    }
}

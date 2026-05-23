package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.service.UserService;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;

    @ModelAttribute("currentUser")
    public User getCurrentUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return userService.findByUsername(principal.getName()).orElse(null);
    }
}

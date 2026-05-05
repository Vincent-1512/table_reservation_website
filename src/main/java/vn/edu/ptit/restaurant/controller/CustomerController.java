package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.dto.CartItem;
import vn.edu.ptit.restaurant.entity.MenuItem;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.service.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final MenuItemService menuItemService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final DiningTableService diningTableService;
    private final ReservationService reservationService;
    private final UserService userService;

    // ================== MENU & CART ================== //
    @GetMapping("/menu")
    public String viewMenu(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("menuItems", menuItemService.findAll());
        model.addAttribute("cartCount", cartService.getCount());
        return "customer/menu";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long menuItemId, @RequestParam(defaultValue = "1") Integer quantity, RedirectAttributes redirectAttrs) {
        MenuItem item = menuItemService.findById(menuItemId).orElseThrow();
        CartItem cartItem = CartItem.builder()
                .menuItemId(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .quantity(quantity)
                .build();
        cartService.add(cartItem);
        redirectAttrs.addFlashAttribute("success", "Đã thêm " + item.getName() + " vào giỏ hàng");
        return "redirect:/menu";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("totalAmount", cartService.getAmount());
        return "customer/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.remove(id);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long menuItemId, @RequestParam Integer quantity) {
        cartService.update(menuItemId, quantity);
        return "redirect:/cart";
    }

    // ================== RESERVATION ================== //
    @GetMapping("/reservation")
    public String viewReservationForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("tables", diningTableService.findAll());
        return "customer/reservation";
    }

    @PostMapping("/reservation")
    public String submitReservation(@RequestParam Long tableId,
                                    @RequestParam String reservationDate,
                                    @RequestParam String reservationTime,
                                    @RequestParam Integer numberOfGuests,
                                    @RequestParam String note,
                                    Principal principal,
                                    RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByUsername(principal.getName()).orElseThrow();
            // Parse datetime
            String dateTimeStr = reservationDate + " " + reservationTime;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime resDateTime = LocalDateTime.parse(dateTimeStr, formatter);

            reservationService.createReservation(user.getId(), tableId, resDateTime, numberOfGuests, note);
            redirectAttrs.addFlashAttribute("success", "Đặt bàn thành công! Hãy đợi nhân viên xác nhận.");
            return "redirect:/my-reservations";

        } catch (ObjectOptimisticLockingFailureException e) {
            // Lỗi kẹt bàn (Race condition) -> Bàn vừa có người đặt xong
            redirectAttrs.addFlashAttribute("error", "Bàn bạn chọn vừa có người khác đặt. Vui lòng chọn bàn khác!");
            return "redirect:/reservation";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservation";
        }
    }

    @GetMapping("/my-reservations")
    public String myReservations(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("reservations", reservationService.findByUserId(user.getId()));
        return "customer/my-reservations";
    }

    @PostMapping("/reservation/cancel")
    public String cancelReservation(@RequestParam Long reservationId, Principal principal, RedirectAttributes redirectAttrs) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        reservationService.cancelReservation(reservationId, user.getId());
        redirectAttrs.addFlashAttribute("success", "Đã hủy bàn thành công.");
        return "redirect:/my-reservations";
    }
}

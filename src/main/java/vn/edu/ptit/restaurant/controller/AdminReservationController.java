package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.service.ReservationService;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public String index(@RequestParam(required = false) ReservationStatus status,
                        Model model) {
        if (status != null) {
            model.addAttribute("reservations", reservationService.findByStatus(status));
        } else {
            model.addAttribute("reservations", reservationService.findAll());
        }

        model.addAttribute("statuses", ReservationStatus.values());
        model.addAttribute("selectedStatus", status);

        return "admin/reservation/index";
    }

    @PostMapping("/{id}/confirm")
    public String confirmReservation(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            reservationService.confirmReservation(id);
            redirectAttrs.addFlashAttribute("success", "Đã xác nhận đặt bàn thành công!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            reservationService.adminCancelReservation(id);
            redirectAttrs.addFlashAttribute("success", "Đã hủy đặt bàn và trả bàn về trạng thái trống.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @PostMapping("/{id}/complete")
    public String completeReservation(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            reservationService.completeReservation(id);
            redirectAttrs.addFlashAttribute("success", "Đã đánh dấu hoàn thành đặt bàn.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/reservations";
    }
}
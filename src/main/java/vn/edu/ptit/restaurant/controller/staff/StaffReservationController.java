package vn.edu.ptit.restaurant.controller.staff;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;
import vn.edu.ptit.restaurant.service.ReservationService;

import java.security.Principal;

@Controller
@RequestMapping("/staff/reservations")
@RequiredArgsConstructor
public class StaffReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public String index(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            @RequestParam(required = false) String phone,
            Model model) {
        
        // Trim phone if it's not null but empty
        if (phone != null && phone.trim().isEmpty()) {
            phone = null;
        }

        model.addAttribute("reservations", reservationService.searchReservations(status, date, phone));
        model.addAttribute("statuses", ReservationStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedPhone", phone);
        return "staff/reservation/index";
    }

    @PostMapping("/{id}/confirm")
    public String confirmReservation(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            reservationService.confirmReservation(id);
            redirectAttrs.addFlashAttribute("success", "Đã xác nhận đặt bàn thành công!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/reservations";
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            reservationService.adminCancelReservation(id);
            redirectAttrs.addFlashAttribute("success", "Đã hủy đặt bàn và trả bàn về trạng thái trống.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/reservations";
    }

    @PostMapping("/{id}/checkin")
    public String checkinReservation(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttrs) {
        try {
            String username = principal != null ? principal.getName() : "admin";
            reservationService.checkinReservation(id, username);
            redirectAttrs.addFlashAttribute("success", "Check-in thành công! Bàn đã được mở phục vụ.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/reservations";
    }
}

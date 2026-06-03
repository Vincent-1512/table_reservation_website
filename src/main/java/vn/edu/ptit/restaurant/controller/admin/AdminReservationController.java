package vn.edu.ptit.restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;
import vn.edu.ptit.restaurant.service.ReservationService;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public String index(@RequestParam(required = false) ReservationStatus status,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

        Page<Reservation> reservationPage = reservationService.searchReservations(
                keyword,
                status,
                startDate,
                endDate,
                page,
                size
        );

        model.addAttribute("reservations", reservationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservationPage.getTotalPages());
        model.addAttribute("size", size);

        model.addAttribute("statuses", ReservationStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

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
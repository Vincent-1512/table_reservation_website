package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.ptit.restaurant.service.ReservationService;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;

@Controller
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping("/admin/reservations")
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
}
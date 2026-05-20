package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.Payment;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.entity.enums.PaymentStatus;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.service.DiningTableService;
import vn.edu.ptit.restaurant.service.OrderService;
import vn.edu.ptit.restaurant.service.PaymentService;
import vn.edu.ptit.restaurant.service.ReservationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/staff/dashboard")
@RequiredArgsConstructor
public class StaffDashboardController {

    private final ReservationService reservationService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final DiningTableService diningTableService;

    @GetMapping
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();

        // Count today's reservations
        List<Reservation> reservations = reservationService.findAll();
        long todayReservationsCount = reservations.stream()
                .filter(r -> r.getReservationTime() != null && r.getReservationTime().toLocalDate().isEqual(today))
                .filter(r -> r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        // Count pending reservations (need attention)
        long pendingReservationsCount = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .count();

        // Count active orders
        List<Order> orders = orderService.findAll();
        long activeOrdersCount = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.SERVING)
                .count();

        // Get active orders list for quick access table
        List<Order> activeOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.SERVING)
                .toList();

        // Calculate today's revenue (from completed payments today)
        List<Payment> payments = paymentService.findAll();
        BigDecimal todayRevenue = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().toLocalDate().isEqual(today))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Table status counts
        long availableTablesCount = diningTableService.findByStatus(TableStatus.AVAILABLE).size();
        long occupiedTablesCount = diningTableService.findByStatus(TableStatus.OCCUPIED).size();

        // Upcoming reservations (today, PENDING or CONFIRMED)
        List<Reservation> upcomingReservations = reservationService.findUpcoming(5);

        model.addAttribute("todayReservationsCount", todayReservationsCount);
        model.addAttribute("pendingReservationsCount", pendingReservationsCount);
        model.addAttribute("activeOrdersCount", activeOrdersCount);
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("availableTablesCount", availableTablesCount);
        model.addAttribute("occupiedTablesCount", occupiedTablesCount);
        model.addAttribute("upcomingReservations", upcomingReservations);

        return "staff/dashboard/index";
    }
}

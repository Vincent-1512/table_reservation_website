package vn.edu.ptit.restaurant.controller.staff;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.restaurant.entity.Payment;
import vn.edu.ptit.restaurant.service.OrderItemService;
import vn.edu.ptit.restaurant.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/staff/invoices")
@RequiredArgsConstructor
public class StaffInvoiceController {

    private final PaymentService paymentService;
    private final OrderItemService orderItemService;

    @GetMapping
    public String index(@RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate,
                        Model model) {
        List<Payment> payments;

        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            payments = paymentService.findCompletedPaymentsByDateRange(start, end);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } else {
            payments = paymentService.findCompletedPayments();
        }

        // Calculate total revenue
        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("payments", payments);
        model.addAttribute("totalRevenue", totalRevenue);

        return "staff/invoice/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Payment payment = paymentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        model.addAttribute("payment", payment);
        model.addAttribute("orderItems", orderItemService.findByOrderId(payment.getOrder().getId()));

        return "staff/invoice/detail";
    }
}

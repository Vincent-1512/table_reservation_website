package vn.edu.ptit.restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.entity.Payment;
import vn.edu.ptit.restaurant.entity.enums.PaymentMethod;
import vn.edu.ptit.restaurant.entity.enums.PaymentStatus;
import vn.edu.ptit.restaurant.service.PaymentService;

import java.time.LocalDate;

import java.util.List;

@Controller
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) PaymentStatus status,
                        @RequestParam(required = false) PaymentMethod method,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

        Page<Payment> paymentPage = paymentService.searchPayments(
                keyword,
                status,
                method,
                startDate,
                endDate,
                page,
                size
        );

        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paymentPage.getTotalPages());
        model.addAttribute("totalItems", paymentPage.getTotalElements());
        model.addAttribute("size", size);

        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("methods", PaymentMethod.values());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedMethod", method);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/payment/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Payment payment = paymentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán"));

        model.addAttribute("payment", payment);
        model.addAttribute("statuses", getAllowedStatuses(payment.getStatus()));

        return "admin/payment/detail";
    }

    private List<PaymentStatus> getAllowedStatuses(PaymentStatus currentStatus) {
        if (currentStatus == PaymentStatus.PENDING) {
            return List.of(PaymentStatus.PENDING, PaymentStatus.PAID, PaymentStatus.FAILED);
        }

        if (currentStatus == PaymentStatus.PAID) {
            return List.of(PaymentStatus.PAID, PaymentStatus.REFUNDED);
        }

        return List.of(currentStatus);
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                            @RequestParam PaymentStatus status,
                            RedirectAttributes redirectAttrs) {
        try {
            paymentService.updateStatus(id, status);
            redirectAttrs.addFlashAttribute("success", "Đã cập nhật trạng thái thanh toán.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/payments/" + id;
    }
}
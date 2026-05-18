package vn.edu.ptit.restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.entity.Payment;
import vn.edu.ptit.restaurant.entity.enums.PaymentStatus;
import vn.edu.ptit.restaurant.service.PaymentService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public String index(@RequestParam(required = false) PaymentStatus status, Model model) {
        List<Payment> payments = paymentService.findAll();

        // Lọc theo status nếu có
        if (status != null) {
            payments = payments.stream()
                    .filter(p -> p.getStatus() == status)
                    .collect(Collectors.toList());
        }

        model.addAttribute("payments", payments);
        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("selectedStatus", status);
        return "admin/payment/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Payment payment = paymentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán"));
        model.addAttribute("payment", payment);
        model.addAttribute("statuses", PaymentStatus.values());
        return "admin/payment/detail";
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
        return "redirect:/admin/payments";
    }
}

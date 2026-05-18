package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.service.OrderService;
import vn.edu.ptit.restaurant.service.OrderItemService;
import vn.edu.ptit.restaurant.service.PaymentService;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final PaymentService paymentService;

    @GetMapping
    public String index(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {

        Page<Order> orderPage = orderService.filterOrders(
                status,
                startDate,
                endDate,
                page,
                size
        );

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());

        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("selectedStatus", status);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/order/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItemService.findByOrderId(id));
        model.addAttribute("payment", paymentService.findByOrderId(id).orElse(null));

        return "admin/order/detail";
    }
}
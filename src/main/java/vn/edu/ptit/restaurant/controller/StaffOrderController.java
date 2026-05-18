package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.entity.enums.PaymentMethod;
import vn.edu.ptit.restaurant.service.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff/orders")
@RequiredArgsConstructor
public class StaffOrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final DiningTableService diningTableService;
    private final MenuItemService menuItemService;
    private final PaymentService paymentService;

    // Danh sách hóa đơn và bàn hiện tại
    @GetMapping
    public String index(Model model) {
        List<Order> allOrders = orderService.findAll();

        // Tạo map: tableId -> orderId cho các bàn đang OCCUPIED (order PENDING hoặc SERVING)
        Map<Long, Long> activeOrderByTable = new HashMap<>();
        for (Order order : allOrders) {
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.SERVING) {
                if (order.getTable() != null) {
                    activeOrderByTable.put(order.getTable().getId(), order.getId());
                }
            }
        }

        model.addAttribute("orders", allOrders);
        model.addAttribute("tables", diningTableService.findAll());
        model.addAttribute("activeOrderByTable", activeOrderByTable);
        return "staff/order/index";
    }

    // Mở một hóa đơn mới cho bàn
    @PostMapping("/create")
    public String createOrder(@RequestParam Long tableId, Principal principal,
                              RedirectAttributes redirectAttrs) {
        try {
            String username = principal != null ? principal.getName() : "admin";
            Order order = orderService.createOrder(tableId, username);
            return "redirect:/staff/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff/orders";
        }
    }

    // Chi tiết hóa đơn & Gọi món
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id).orElseThrow();
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItemService.findByOrderId(id));
        model.addAttribute("menuItems", menuItemService.findAll());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        // Kiểm tra đã thanh toán chưa
        model.addAttribute("payment", paymentService.findByOrderId(id).orElse(null));
        model.addAttribute("orderStatuses", new OrderStatus[]{OrderStatus.PENDING, OrderStatus.SERVING});
        return "staff/order/detail";
    }

    // Thêm món vào hóa đơn
    @PostMapping("/{id}/add-item")
    public String addItem(@PathVariable Long id,
                          @RequestParam Long menuItemId,
                          @RequestParam Integer quantity,
                          @RequestParam(required = false) String note,
                          RedirectAttributes redirectAttrs) {
        try {
            orderItemService.addMenuItemToOrder(id, menuItemId, quantity, note);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/orders/" + id;
    }

    // Xóa món khỏi hóa đơn
    @GetMapping("/{orderId}/delete-item/{itemId}")
    public String deleteItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        orderItemService.deleteOrderItem(itemId);
        return "redirect:/staff/orders/" + orderId;
    }

    // Cập nhật trạng thái order (PENDING → SERVING)
    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               RedirectAttributes redirectAttrs) {
        try {
            orderService.updateOrderStatus(id, status);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/orders/" + id;
    }

    // Thanh toán hóa đơn (Checkout) — tạo payment trước, rồi complete order
    @PostMapping("/{id}/checkout")
    public String checkoutOrder(@PathVariable Long id,
                                @RequestParam PaymentMethod paymentMethod,
                                RedirectAttributes redirectAttrs) {
        try {
            // Tạo payment record trước
            paymentService.createPayment(id, paymentMethod);
            // Sau đó đóng bàn (set COMPLETED + free table)
            orderService.checkout(id);
            redirectAttrs.addFlashAttribute("success", "Thanh toán thành công! Bàn đã được giải phóng.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/staff/orders/" + id;
        }
        return "redirect:/staff/orders";
    }
}

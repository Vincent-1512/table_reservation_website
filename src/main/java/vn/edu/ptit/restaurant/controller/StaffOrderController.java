package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.service.DiningTableService;
import vn.edu.ptit.restaurant.service.MenuItemService;
import vn.edu.ptit.restaurant.service.OrderItemService;
import vn.edu.ptit.restaurant.service.OrderService;

import java.security.Principal;

@Controller
@RequestMapping("/staff/orders")
@RequiredArgsConstructor
public class StaffOrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final DiningTableService diningTableService;
    private final MenuItemService menuItemService;

    // Danh sách hóa đơn và bàn hiện tại
    @GetMapping
    public String index(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("tables", diningTableService.findAll());
        return "staff/order/index";
    }

    // Mở một hóa đơn mới cho bàn
    @PostMapping("/create")
    public String createOrder(@RequestParam Long tableId, Principal principal) {
        // Trong đồ án thực tế, lấy username từ session đăng nhập của nhân viên
        String username = principal != null ? principal.getName() : "admin";
        Order order = orderService.createOrder(tableId, username);
        return "redirect:/staff/orders/" + order.getId();
    }

    // Chi tiết hóa đơn & Gọi món
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id).orElseThrow();
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItemService.findByOrderId(id));
        model.addAttribute("menuItems", menuItemService.findAll());
        return "staff/order/detail";
    }

    // Thêm món vào hóa đơn
    @PostMapping("/{id}/add-item")
    public String addItem(@PathVariable Long id, 
                          @RequestParam Long menuItemId, 
                          @RequestParam Integer quantity, 
                          @RequestParam(required = false) String note) {
        orderItemService.addMenuItemToOrder(id, menuItemId, quantity, note);
        return "redirect:/staff/orders/" + id;
    }

    // Xóa món khỏi hóa đơn (Nếu khách đổi ý ngay lúc đó)
    @GetMapping("/{orderId}/delete-item/{itemId}")
    public String deleteItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        orderItemService.deleteOrderItem(itemId);
        return "redirect:/staff/orders/" + orderId;
    }

    // Thanh toán hóa đơn (Checkout)
    @PostMapping("/{id}/checkout")
    public String checkoutOrder(@PathVariable Long id) {
        orderService.checkout(id);
        return "redirect:/staff/orders";
    }
}

package vn.edu.ptit.restaurant.controller.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.dto.CartItem;
import vn.edu.ptit.restaurant.entity.MenuItem;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.repository.OrderRepository;
import vn.edu.ptit.restaurant.repository.OrderItemRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.ptit.restaurant.service.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import vn.edu.ptit.restaurant.entity.OrderItem;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final MenuItemService menuItemService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final DiningTableService diningTableService;
    private final ReservationService reservationService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;

    // ================== MENU & CART ================== //
    @GetMapping("/menu")
    public String viewMenu(Model model, Principal principal) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("menuItems", menuItemService.findAll());
        model.addAttribute("cartCount", cartService.getCount());
        
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("user", user);
        } else {
            model.addAttribute("user", new User()); // Prevent null reference exceptions in Thymeleaf
        }
        model.addAttribute("tables", diningTableService.findAll());
        
        return "customer/menu";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long menuItemId, @RequestParam(defaultValue = "1") Integer quantity, RedirectAttributes redirectAttrs) {
        MenuItem item = menuItemService.findById(menuItemId).orElseThrow();
        CartItem cartItem = CartItem.builder()
                .menuItemId(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .quantity(quantity)
                .build();
        cartService.add(cartItem);
        redirectAttrs.addFlashAttribute("success", "Đã thêm " + item.getName() + " vào giỏ hàng");
        return "redirect:/menu";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("totalAmount", cartService.getAmount());
        return "customer/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.remove(id);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long menuItemId, @RequestParam Integer quantity) {
        cartService.update(menuItemId, quantity);
        return "redirect:/cart";
    }

    // ================== RESERVATION ================== //
    @GetMapping("/reservation")
    public String viewReservationForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("tables", diningTableService.findAll());
        return "customer/reservation";
    }

    @PostMapping("/reservation")
    public String submitReservation(@RequestParam Long tableId,
                                    @RequestParam String reservationDate,
                                    @RequestParam String reservationTime,
                                    @RequestParam Integer numberOfGuests,
                                    @RequestParam String note,
                                    @RequestParam String fullName,
                                    @RequestParam String phone,
                                    @RequestParam String email,
                                    Principal principal,
                                    RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByUsername(principal.getName()).orElseThrow();

            // Đồng bộ thông tin cá nhân của người dùng nếu có thay đổi
            boolean profileChanged = false;
            if (!fullName.equals(user.getFullName())) {
                user.setFullName(fullName);
                profileChanged = true;
            }
            if (phone != null && !phone.equals(user.getPhone())) {
                user.setPhone(phone);
                profileChanged = true;
            }
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                profileChanged = true;
            }
            if (profileChanged) {
                userService.save(user);
            }

            // Parse datetime
            String dateTimeStr = reservationDate + " " + reservationTime;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime resDateTime = LocalDateTime.parse(dateTimeStr, formatter);

            Reservation reservation = reservationService.createReservation(user.getId(), tableId, resDateTime, numberOfGuests, note);

            // Tự động giữ món trong giỏ hàng nếu có
            if (!cartService.getItems().isEmpty()) {
                reservationService.createOrderForReservation(reservation.getId(), user.getUsername());
                return "redirect:/reservation/payment/food?id=" + reservation.getId();
            }

            return "redirect:/reservation/success?id=" + reservation.getId();

        } catch (ObjectOptimisticLockingFailureException e) {
            redirectAttrs.addFlashAttribute("error", "Bàn bạn chọn vừa có người khác đặt. Vui lòng chọn bàn khác!");
            return cartService.getItems().isEmpty() ? "redirect:/reservation" : "redirect:/menu";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return cartService.getItems().isEmpty() ? "redirect:/reservation" : "redirect:/menu";
        }
    }

    @GetMapping("/reservation/success")
    public String showReservationSuccessPage(@RequestParam Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Reservation reservation = reservationService.findById(id);
        if (!reservation.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/my-reservations";
        }

        long depositAmount = 100000L;
        model.addAttribute("reservation", reservation);
        model.addAttribute("depositAmount", depositAmount);

        return "customer/reservation-success";
    }

    @GetMapping("/reservation/payment")
    public String showPaymentPage(@RequestParam Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Reservation reservation = reservationService.findById(id);
        if (!reservation.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/my-reservations";
        }

        long depositAmount = 100000L;
        model.addAttribute("reservation", reservation);
        model.addAttribute("depositAmount", depositAmount);
        model.addAttribute("totalAmount", java.math.BigDecimal.valueOf(depositAmount));

        return "customer/deposit-payment";
    }

    @PostMapping("/reservation/payment")
    public String processPayment(@RequestParam Long id, Principal principal, RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            Reservation reservation = reservationService.findById(id);
            if (!reservation.getUser().getUsername().equals(principal.getName())) {
                redirectAttrs.addFlashAttribute("error", "Không có quyền thanh toán.");
                return "redirect:/my-reservations";
            }

            // Thanh toán thành công -> chuyển sang CONFIRMED
            reservationService.confirmReservation(id);
            redirectAttrs.addFlashAttribute("success", "Thanh toán đặt cọc thành công!");
            return "redirect:/reservation/success?id=" + id;
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Thanh toán thất bại: " + e.getMessage());
            return "redirect:/reservation/payment?id=" + id;
        }
    }

    @PostMapping("/reservation/order")
    public String createOrderForReservation(@RequestParam Long reservationId, Principal principal, RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            reservationService.createOrderForReservation(reservationId, principal.getName());
            return "redirect:/reservation/payment/food?id=" + reservationId;
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi đặt món ăn: " + e.getMessage());
            return "redirect:/menu?flow=booking&reservationId=" + reservationId;
        }
    }

    @GetMapping("/reservation/payment/food")
    public String showFoodPaymentPage(@RequestParam Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Reservation reservation = reservationService.findById(id);
        if (!reservation.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/my-reservations";
        }

        Optional<Order> orderOpt = orderRepository.findByReservationId(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/menu?flow=booking&reservationId=" + id;
        }

        Order order = orderOpt.get();
        long depositAmount = 100000L;
        long totalFoodAmount = order.getTotalAmount().longValue();
        long totalBill = totalFoodAmount; // Chỉ thanh toán tiền món ăn theo yêu cầu

        model.addAttribute("reservation", reservation);
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItemRepository.findByOrderId(order.getId()));
        model.addAttribute("depositAmount", depositAmount);
        model.addAttribute("totalFoodAmount", totalFoodAmount);
        model.addAttribute("totalBill", totalBill);
        model.addAttribute("totalAmount", order.getTotalAmount());

        return "customer/food-payment";
    }

    @PostMapping("/reservation/payment/food")
    public String processFoodPayment(@RequestParam Long id, @RequestParam(defaultValue = "full") String paymentMode, Principal principal, RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            Reservation reservation = reservationService.findById(id);
            if (!reservation.getUser().getUsername().equals(principal.getName())) {
                redirectAttrs.addFlashAttribute("error", "Không có quyền thanh toán.");
                return "redirect:/my-reservations";
            }

            // Thanh toán thành công -> chuyển sang CONFIRMED
            reservationService.confirmReservation(id);

            // Cập nhật trạng thái Order liên kết
            Optional<Order> orderOpt = orderRepository.findByReservationId(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                if ("full".equalsIgnoreCase(paymentMode)) {
                    order.setStatus(OrderStatus.CONFIRMED);
                } else {
                    order.setStatus(OrderStatus.PENDING); // Khách chỉ đặt cọc cọc, tiền ăn sẽ trả sau ở nhà hàng
                }
                orderRepository.save(order);
            }

            redirectAttrs.addFlashAttribute("success", "Thanh toán thành công đơn đặt món ăn!");
            return "redirect:/my-reservations";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Thanh toán thất bại: " + e.getMessage());
            return "redirect:/reservation/payment/food?id=" + id;
        }
    }

    @GetMapping("/my-reservations")
    public String myReservations(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        
        // Truy vấn danh sách đặt bàn
        model.addAttribute("reservations", reservationService.findByUserId(user.getId()));
        
        // Truy vấn danh sách đơn món ăn
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        Map<Long, List<OrderItem>> orderItemsMap = new HashMap<>();
        for (Order order : orders) {
            orderItemsMap.put(order.getId(), orderItemRepository.findByOrderId(order.getId()));
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("orderItemsMap", orderItemsMap);
        
        return "customer/my-reservations";
    }

    @PostMapping("/reservation/cancel")
    public String cancelReservation(@RequestParam Long reservationId, Principal principal, RedirectAttributes redirectAttrs) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        reservationService.cancelReservation(reservationId, user.getId());
        redirectAttrs.addFlashAttribute("success", "Đã hủy bàn thành công.");
        return "redirect:/my-reservations";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "customer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam String email,
                                Principal principal,
                                RedirectAttributes redirectAttrs) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userService.findByUsername(principal.getName()).orElseThrow();
            
            // Validate
            if (fullName.trim().isEmpty()) {
                redirectAttrs.addFlashAttribute("error", "Họ tên không được để trống.");
                return "redirect:/profile";
            }
            if (phone.trim().isEmpty() || !phone.matches("^[0-9]{9,11}$")) {
                redirectAttrs.addFlashAttribute("error", "Số điện thoại không hợp lệ (phải gồm 9-11 chữ số).");
                return "redirect:/profile";
            }
            if (email.trim().isEmpty() || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                redirectAttrs.addFlashAttribute("error", "Địa chỉ email không hợp lệ.");
                return "redirect:/profile";
            }

            user.setFullName(fullName.trim());
            user.setPhone(phone.trim());
            user.setEmail(email.trim());
            userService.save(user);

            redirectAttrs.addFlashAttribute("success", "Đã cập nhật thông tin cá nhân thành công.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttrs) {
        if (principal == null) return "redirect:/login";
        try {
            User user = userService.findByUsername(principal.getName()).orElseThrow();

            // Validate current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttrs.addFlashAttribute("error", "Mật khẩu hiện tại không chính xác.");
                return "redirect:/profile";
            }

            // Validate new password length
            if (newPassword == null || newPassword.length() < 6) {
                redirectAttrs.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
                return "redirect:/profile";
            }

            // Validate match
            if (!newPassword.equals(confirmPassword)) {
                redirectAttrs.addFlashAttribute("error", "Mật khẩu mới và mật khẩu xác nhận không khớp.");
                return "redirect:/profile";
            }

            // Save new password
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.save(user);

            redirectAttrs.addFlashAttribute("success", "Đã thay đổi mật khẩu thành công.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/avatar/upload")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file, Principal principal, RedirectAttributes redirectAttrs) {
        if (principal == null) return "redirect:/login";
        if (file.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "File tải lên không được để trống.");
            return "redirect:/profile";
        }
        
        try {
            User user = userService.findByUsername(principal.getName()).orElseThrow();
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                redirectAttrs.addFlashAttribute("error", "Chỉ cho phép tải lên các file định dạng hình ảnh.");
                return "redirect:/profile";
            }
            
            // Create target directory if it doesn't exist
            String uploadDir = "src/main/resources/static/uploads/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String fileExtension = "";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Also copy to target classes dir so it's served immediately without restart!
            try {
                String targetDir = "target/classes/static/uploads/avatars/";
                Path targetPath = Paths.get(targetDir);
                if (!Files.exists(targetPath)) {
                    Files.createDirectories(targetPath);
                }
                Files.copy(filePath, targetPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                // Ignore copying to target if failed
            }
            
            // Update user entity
            String relativeUrl = "/uploads/avatars/" + filename;
            user.setAvatarUrl(relativeUrl);
            userService.save(user);
            
            redirectAttrs.addFlashAttribute("success", "Đã tải lên ảnh đại diện thành công!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}

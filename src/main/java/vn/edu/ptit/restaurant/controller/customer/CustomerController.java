package vn.edu.ptit.restaurant.controller.customer;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.dto.CartItem;
import vn.edu.ptit.restaurant.dto.request.ChangePasswordRequest;
import vn.edu.ptit.restaurant.dto.request.ProfileUpdateRequest;
import vn.edu.ptit.restaurant.dto.request.ReservationRequest;
import vn.edu.ptit.restaurant.entity.MenuItem;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.repository.OrderRepository;
import vn.edu.ptit.restaurant.repository.OrderItemRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.ptit.restaurant.service.*;
import vn.edu.ptit.restaurant.validator.customer.CustomerProfileValidator;

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
    private final CustomerProfileValidator customerProfileValidator;

    // ================== MENU & CART ================== //
    @GetMapping("/menu")
    public String viewMenu(@RequestParam(required = false) String flow,
                           @RequestParam(required = false) Long reservationId,
                           Model model,
                           Principal principal) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("menuItems", menuItemService.findAll());
        model.addAttribute("cartCount", cartService.getCount());
        model.addAttribute("flow", flow);
        model.addAttribute("reservationId", reservationId);
        
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
    public String addToCart(@RequestParam Long menuItemId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            @RequestParam(required = false) String flow,
                            @RequestParam(required = false) Long reservationId,
                            @RequestParam(required = false) String note,
                            RedirectAttributes redirectAttrs) {
        MenuItem item = menuItemService.findById(menuItemId).orElseThrow();
        CartItem cartItem = CartItem.builder()
                .menuItemId(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .quantity(quantity)
                .note(note)
                .build();
        cartService.add(cartItem);
        redirectAttrs.addFlashAttribute("success", "Đã thêm " + item.getName() + " vào giỏ hàng");
        if ("booking".equals(flow) && reservationId != null) {
            return "redirect:/menu?flow=booking&reservationId=" + reservationId;
        }
        return "redirect:/menu";
    }

    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCartApi(@RequestParam Long menuItemId,
                                                            @RequestParam(defaultValue = "1") Integer quantity,
                                                            @RequestParam(required = false) String note) {
        MenuItem item = menuItemService.findById(menuItemId).orElseThrow();
        CartItem cartItem = CartItem.builder()
                .menuItemId(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .quantity(quantity)
                .note(note)
                .build();
        cartService.add(cartItem);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Da them " + item.getName() + " vao gio hang");
        response.put("count", cartService.getCount());
        response.put("totalAmount", cartService.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cart")
    public String viewCart(@RequestParam(required = false) String flow,
                           @RequestParam(required = false) Long reservationId,
                           Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("totalAmount", cartService.getAmount());
        model.addAttribute("flow", flow);
        model.addAttribute("reservationId", reservationId);
        return "customer/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id,
                                 @RequestParam(required = false) String flow,
                                 @RequestParam(required = false) Long reservationId) {
        cartService.remove(id);
        if ("booking".equals(flow) && reservationId != null) {
            return "redirect:/cart?flow=" + flow + "&reservationId=" + reservationId;
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long menuItemId,
                             @RequestParam Integer quantity,
                             @RequestParam(required = false) String note,
                             @RequestParam(required = false) String flow,
                             @RequestParam(required = false) Long reservationId) {
        cartService.update(menuItemId, quantity, note);
        if ("booking".equals(flow) && reservationId != null) {
            return "redirect:/cart?flow=" + flow + "&reservationId=" + reservationId;
        }
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
    public String submitReservation(@Valid @ModelAttribute ReservationRequest request,
                                    BindingResult bindingResult,
                                    Principal principal,
                                    RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            if (bindingResult.hasErrors()) {
                redirectAttrs.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
                return reservationRetryRedirect();
            }

            User user = userService.findByUsername(principal.getName()).orElseThrow();

            // Đồng bộ thông tin cá nhân của người dùng nếu có thay đổi
            boolean profileChanged = false;
            if (request.getFullName() != null && !request.getFullName().equals(user.getFullName())) {
                user.setFullName(request.getFullName());
                profileChanged = true;
            }
            if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
                user.setPhone(request.getPhone());
                profileChanged = true;
            }
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                user.setEmail(request.getEmail());
                profileChanged = true;
            }
            if (profileChanged) {
                userService.save(user);
            }

            // Parse datetime
            String dateTimeStr = request.getReservationDate() + " " + request.getReservationTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime resDateTime = LocalDateTime.parse(dateTimeStr, formatter);

            boolean hasCart = !cartService.getItems().isEmpty();
            Reservation reservation = reservationService.createReservation(
                    user.getId(),
                    request.getTableId(),
                    resDateTime,
                    request.getNumberOfGuests(),
                    request.getNote()
            );

            // Tự động giữ món trong giỏ hàng nếu có
            if (hasCart) {
                return "redirect:/reservation/payment/food?id=" + reservation.getId();
            }

            return "redirect:/reservation/success?id=" + reservation.getId();

        } catch (ObjectOptimisticLockingFailureException e) {
            redirectAttrs.addFlashAttribute("error", "Bàn bạn chọn vừa có người khác đặt. Vui lòng chọn bàn khác!");
            return reservationRetryRedirect();
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return reservationRetryRedirect();
        }
    }

    private String reservationRetryRedirect() {
        return cartService.getItems().isEmpty() ? "redirect:/reservation" : "redirect:/menu";
    }

    @GetMapping("/reservation/success")
    public String showReservationSuccessPage(@RequestParam Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Reservation reservation;
        try {
            reservation = reservationService.findByIdForUser(id, principal.getName());
        } catch (Exception e) {
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
        Reservation reservation;
        try {
            reservation = reservationService.findByIdForUser(id, principal.getName());
        } catch (Exception e) {
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
            reservationService.processDepositPayment(id, principal.getName());
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
        Reservation reservation;
        try {
            reservation = reservationService.findByIdForUser(id, principal.getName());
        } catch (Exception e) {
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
            reservationService.processFoodPayment(id, principal.getName(), paymentMode);

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
        Map<Long, Order> reservationOrderMap = new HashMap<>();
        Map<Long, List<OrderItem>> orderItemsMap = new HashMap<>();
        for (Order order : orders) {
            if (order.getReservation() != null) {
                reservationOrderMap.put(order.getReservation().getId(), order);
            }
            orderItemsMap.put(order.getId(), orderItemRepository.findByOrderId(order.getId()));
        }
        
        model.addAttribute("reservationOrderMap", reservationOrderMap);
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
    public String updateProfile(@Valid @ModelAttribute ProfileUpdateRequest request,
                                BindingResult bindingResult,
                                Principal principal,
                                RedirectAttributes redirectAttrs) {
        if (principal == null) return "redirect:/login";
        try {
            if (bindingResult.hasErrors()) {
                redirectAttrs.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
                return "redirect:/profile";
            }

            User user = userService.findByUsername(principal.getName()).orElseThrow();
            user.setFullName(request.getFullName().trim());
            user.setPhone(request.getPhone().trim());
            user.setEmail(request.getEmail().trim());
            userService.save(user);

            redirectAttrs.addFlashAttribute("success", "Đã cập nhật thông tin cá nhân thành công.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordRequest request,
                                 BindingResult bindingResult,
                                 Principal principal,
                                 RedirectAttributes redirectAttrs) {
        if (principal == null) return "redirect:/login";
        try {
            if (bindingResult.hasErrors()) {
                redirectAttrs.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
                return "redirect:/profile";
            }

            User user = userService.findByUsername(principal.getName()).orElseThrow();

            customerProfileValidator.validatePasswordChange(user, request, passwordEncoder);

            // Save new password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
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
        try {
            User user = userService.findByUsername(principal.getName()).orElseThrow();
            
            customerProfileValidator.validateAvatar(file);
            
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

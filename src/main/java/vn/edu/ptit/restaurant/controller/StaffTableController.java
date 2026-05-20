package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.service.AreaService;
import vn.edu.ptit.restaurant.service.DiningTableService;
import vn.edu.ptit.restaurant.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff/tables")
@RequiredArgsConstructor
public class StaffTableController {

    private final DiningTableService diningTableService;
    private final AreaService areaService;
    private final OrderService orderService;

    @GetMapping
    public String index(@RequestParam(required = false) String statusFilter, Model model) {
        List<DiningTable> tables;
        if (statusFilter != null && !statusFilter.isEmpty()) {
            tables = diningTableService.findByStatus(TableStatus.valueOf(statusFilter));
        } else {
            tables = diningTableService.findAll();
        }

        // Build active order map: tableId -> order
        Map<Long, Order> activeOrderByTable = new HashMap<>();
        List<Order> allOrders = orderService.findAll();
        for (Order order : allOrders) {
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.SERVING) {
                if (order.getTable() != null) {
                    activeOrderByTable.put(order.getTable().getId(), order);
                }
            }
        }

        // Count by status
        long availableCount = diningTableService.findByStatus(TableStatus.AVAILABLE).size();
        long occupiedCount = diningTableService.findByStatus(TableStatus.OCCUPIED).size();
        long reservedCount = diningTableService.findByStatus(TableStatus.RESERVED).size();
        long maintenanceCount = diningTableService.findByStatus(TableStatus.MAINTENANCE).size();

        model.addAttribute("tables", tables);
        model.addAttribute("areas", areaService.findAll());
        model.addAttribute("statuses", TableStatus.values());
        model.addAttribute("selectedStatus", statusFilter);
        model.addAttribute("activeOrderByTable", activeOrderByTable);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("occupiedCount", occupiedCount);
        model.addAttribute("reservedCount", reservedCount);
        model.addAttribute("maintenanceCount", maintenanceCount);

        return "staff/table/index";
    }
}

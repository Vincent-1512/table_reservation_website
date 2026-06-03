package vn.edu.ptit.restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.ptit.restaurant.entity.Area;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.service.AreaService;
import vn.edu.ptit.restaurant.service.DiningTableService;

@Controller
@RequestMapping("/admin/tables")
@RequiredArgsConstructor
public class AdminTableController {

    private final AreaService areaService;
    private final DiningTableService diningTableService;

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Integer areaId,
                        @RequestParam(required = false) TableStatus status,
                        @RequestParam(required = false) Integer minCapacity,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

        Page<DiningTable> tablePage = diningTableService.searchTables(
                keyword,
                areaId,
                status,
                minCapacity,
                page,
                size
        );

        model.addAttribute("tables", tablePage.getContent());
        model.addAttribute("areas", areaService.findAll());
        model.addAttribute("statuses", TableStatus.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tablePage.getTotalPages());
        model.addAttribute("totalItems", tablePage.getTotalElements());
        model.addAttribute("size", size);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedAreaId", areaId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("minCapacity", minCapacity);

        return "admin/table/index";
    }

    // ================= AREA ================= //

    @GetMapping("/area/add")
    public String showAddAreaForm(Model model) {
        model.addAttribute("area", new Area());
        return "admin/table/area-form";
    }

    @PostMapping("/area/save")
    public String saveArea(@ModelAttribute Area area, RedirectAttributes redirectAttrs) {
        try {
            areaService.save(area);
            redirectAttrs.addFlashAttribute("success", "Lưu khu vực thành công");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/tables";
    }

    @GetMapping("/area/edit/{id}")
    public String showEditAreaForm(@PathVariable Integer id, Model model) {
        areaService.findById(id).ifPresent(a -> model.addAttribute("area", a));
        return "admin/table/area-form";
    }

    @PostMapping("/area/delete/{id}")
    public String deleteArea(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        try {
            areaService.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Xóa khu vực thành công");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/tables";
    }

    // ================= TABLE ================= //

    @GetMapping("/add")
    public String showAddTableForm(Model model) {
        model.addAttribute("diningTable", new DiningTable());
        model.addAttribute("areas", areaService.findAll());
        model.addAttribute("statuses", TableStatus.values());
        return "admin/table/table-form";
    }

    @PostMapping("/save")
    public String saveTable(@ModelAttribute DiningTable diningTable, RedirectAttributes redirectAttrs) {
        try {
            diningTableService.save(diningTable);
            redirectAttrs.addFlashAttribute("success", "Lưu bàn thành công");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/tables";
    }

    @GetMapping("/edit/{id}")
    public String showEditTableForm(@PathVariable Long id, Model model) {
        diningTableService.findById(id).ifPresent(t -> model.addAttribute("diningTable", t));
        model.addAttribute("areas", areaService.findAll());
        model.addAttribute("statuses", TableStatus.values());
        return "admin/table/table-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteTable(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            diningTableService.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Xóa bàn thành công");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/admin/tables";
    }
}
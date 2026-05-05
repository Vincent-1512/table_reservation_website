package vn.edu.ptit.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.restaurant.entity.Area;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.service.AreaService;
import vn.edu.ptit.restaurant.service.DiningTableService;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;

@Controller
@RequestMapping("/admin/tables")
@RequiredArgsConstructor
public class AdminTableController {

    private final AreaService areaService;
    private final DiningTableService diningTableService;

    // Hiển thị danh sách Khu vực và Bàn
    @GetMapping
    public String index(Model model) {
        model.addAttribute("areas", areaService.findAll());
        model.addAttribute("tables", diningTableService.findAll());
        return "admin/table/index";
    }

    // ================= AREA ================= //
    @GetMapping("/area/add")
    public String showAddAreaForm(Model model) {
        model.addAttribute("area", new Area());
        return "admin/table/area-form";
    }

    @PostMapping("/area/save")
    public String saveArea(@ModelAttribute Area area) {
        areaService.save(area);
        return "redirect:/admin/tables";
    }

    @GetMapping("/area/edit/{id}")
    public String showEditAreaForm(@PathVariable Integer id, Model model) {
        areaService.findById(id).ifPresent(a -> model.addAttribute("area", a));
        return "admin/table/area-form";
    }

    @GetMapping("/area/delete/{id}")
    public String deleteArea(@PathVariable Integer id) {
        areaService.deleteById(id);
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
    public String saveTable(@ModelAttribute DiningTable diningTable) {
        diningTableService.save(diningTable);
        return "redirect:/admin/tables";
    }

    @GetMapping("/edit/{id}")
    public String showEditTableForm(@PathVariable Long id, Model model) {
        diningTableService.findById(id).ifPresent(t -> model.addAttribute("diningTable", t));
        model.addAttribute("areas", areaService.findAll());
        model.addAttribute("statuses", TableStatus.values());
        return "admin/table/table-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable Long id) {
        diningTableService.deleteById(id);
        return "redirect:/admin/tables";
    }
}

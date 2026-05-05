package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.Area;
import java.util.List;
import java.util.Optional;

public interface AreaService {
    List<Area> findAll();
    Optional<Area> findById(Integer id);
    Area save(Area area);
    void deleteById(Integer id);
}

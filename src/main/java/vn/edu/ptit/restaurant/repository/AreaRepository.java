package vn.edu.ptit.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {
}

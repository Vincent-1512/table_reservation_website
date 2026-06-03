package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.repository.DiningTableRepository;
import vn.edu.ptit.restaurant.repository.OrderRepository;
import vn.edu.ptit.restaurant.repository.ReservationRepository;
import vn.edu.ptit.restaurant.service.DiningTableService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl implements DiningTableService {

    private final DiningTableRepository diningTableRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<DiningTable> findAll() {
        return diningTableRepository.findAll();
    }

    @Override
    public List<DiningTable> findByAreaId(Integer areaId) {
        return diningTableRepository.findByAreaId(areaId);
    }

    @Override
    public List<DiningTable> findByAreaIdAndStatus(Integer areaId, TableStatus status) {
        return diningTableRepository.findByAreaIdAndStatus(areaId, status);
    }

    @Override
    public List<DiningTable> findByStatus(TableStatus status) {
        return diningTableRepository.findByStatus(status);
    }

    @Override
    public Page<DiningTable> searchTables(String keyword,
                                          Integer areaId,
                                          TableStatus status,
                                          Integer minCapacity,
                                          int page,
                                          int size) {
        Pageable pageable = PageRequest.of(page, size);
        String cleanKeyword = keyword == null ? null : keyword.trim();

        return diningTableRepository.searchTables(
                cleanKeyword,
                areaId,
                status,
                minCapacity,
                pageable
        );
    }

    @Override
    public Optional<DiningTable> findById(Long id) {
        return diningTableRepository.findById(id);
    }

    @Override
    @Transactional
    public DiningTable save(DiningTable diningTable) {
        if (diningTable.getArea() == null || diningTable.getArea().getId() == null) {
            throw new RuntimeException("Vui lòng chọn khu vực cho bàn");
        }

        if (diningTable.getTableName() == null || diningTable.getTableName().trim().isEmpty()) {
            throw new RuntimeException("Tên bàn không được để trống");
        }

        if (diningTable.getCapacity() == null || diningTable.getCapacity() <= 0) {
            throw new RuntimeException("Sức chứa phải lớn hơn 0");
        }

        List<DiningTable> duplicatedTables = diningTableRepository
                .findByAreaIdAndTableNameIgnoreCase(
                        diningTable.getArea().getId(),
                        diningTable.getTableName().trim()
                );

        boolean duplicated = duplicatedTables.stream()
                .anyMatch(t -> diningTable.getId() == null || !t.getId().equals(diningTable.getId()));

        if (duplicated) {
            throw new RuntimeException("Tên bàn đã tồn tại trong khu vực này");
        }

        if (diningTable.getStatus() == null) {
            diningTable.setStatus(TableStatus.AVAILABLE);
        }

        diningTable.setTableName(diningTable.getTableName().trim());

        return diningTableRepository.save(diningTable);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        DiningTable table = diningTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

        if (table.getStatus() == TableStatus.OCCUPIED || table.getStatus() == TableStatus.RESERVED) {
            throw new RuntimeException("Không thể xóa bàn đang có khách hoặc đang được đặt");
        }

        boolean hasReservation = reservationRepository.existsByTableId(id);
        boolean hasOrder = orderRepository.existsByTableId(id);

        if (hasReservation || hasOrder) {
            throw new RuntimeException("Không thể xóa bàn đã phát sinh đặt bàn hoặc hóa đơn. Hãy chuyển bàn sang trạng thái BẢO TRÌ nếu không còn sử dụng.");
        }

        diningTableRepository.deleteById(id);
    }
}
package vn.edu.ptit.restaurant.service;

import org.springframework.data.domain.Page;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerNewUserAccount(User user);
    Optional<User> findByUsername(String username);
    void createAdminAccountIfNotExist();
    List<User> findByRole(Role role);
    List<User> findAll();
    User findById(Long id);
    void updateRole(Long id, Role role);
    void deleteById(Long id);
    List<User> search(String keyword);
    Page<User> findPaginated(int page, int size);
    User save(User user);
}
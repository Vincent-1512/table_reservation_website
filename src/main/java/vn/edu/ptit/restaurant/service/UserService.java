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

    User createUserByAdmin(User user);

    List<User> findByRole(Role role);

    List<User> findAll();

    User findById(Long id);

    void updateRole(Long id, Role role);

    void updateRole(Long id, Role role, String currentUsername);

    void deleteById(Long id);

    void deleteById(Long id, String currentUsername);

    List<User> search(String keyword);

    Page<User> findPaginated(int page, int size);
    Page<User> searchActiveUsers(String keyword, Role role, int page, int size);
    void updateProfile(String username, String fullName, String phone, String email);

    void changePassword(String username, String oldPassword, String newPassword);

    User save(User user);
}
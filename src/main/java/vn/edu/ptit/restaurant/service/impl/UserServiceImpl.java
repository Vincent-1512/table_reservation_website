package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.enums.Role;
import vn.edu.ptit.restaurant.repository.UserRepository;
import vn.edu.ptit.restaurant.service.UserService;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerNewUserAccount(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CUSTOMER);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @PostConstruct
    public void createAdminAccountIfNotExist() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Quản trị viên")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Đã khởi tạo tài khoản ADMIN mặc định: admin / 123456");
        }
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public void updateRole(Long id, Role role) {
        User user = findById(id);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        // Soft delete: set deleted flag instead of physical removal
        User user = findById(id);
        user.setDeleted(true);
        userRepository.save(user);

    }

    @Override
    public List<User> search(String keyword) {
        return userRepository
                .findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword, keyword
                );
    }

    @Override
    public Page<User> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAll(pageable);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}

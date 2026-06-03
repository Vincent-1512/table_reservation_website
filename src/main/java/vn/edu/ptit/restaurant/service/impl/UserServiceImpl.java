package vn.edu.ptit.restaurant.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.enums.Role;
import vn.edu.ptit.restaurant.repository.UserRepository;
import vn.edu.ptit.restaurant.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerNewUserAccount(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setDeleted(false);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username);
    }

    @Override
    public User createUserByAdmin(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Tên đăng nhập không được để trống");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Họ tên không được để trống");
        }

        String username = user.getUsername().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            String email = user.getEmail().trim();

            if (userRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("Email đã tồn tại");
            }

            user.setEmail(email);
        }

        user.setUsername(username);
        user.setFullName(user.getFullName().trim());

        if (user.getPhone() != null) {
            user.setPhone(user.getPhone().trim());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }

        user.setDeleted(false);

        return userRepository.save(user);
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
                    .deleted(false)
                    .build();

            userRepository.save(admin);
            System.out.println("Đã khởi tạo tài khoản ADMIN mặc định: admin / 123456");
        }
    }

    @Override
    public List<User> findAll() {
        return userRepository.findByDeletedFalse();
    }

    @Override
    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (Boolean.TRUE.equals(user.getDeleted())) {
            throw new RuntimeException("Người dùng này đã bị xóa");
        }

        return user;
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.searchActiveUsers(null, role, Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional
    public void updateRole(Long id, Role role) {
        User user = findById(id);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateRole(Long id, Role role, String currentUsername) {
        User user = findById(id);

        if (user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Bạn không thể tự đổi quyền của chính mình");
        }

        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = findById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id, String currentUsername) {
        User user = findById(id);

        if (user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Bạn không thể tự xóa tài khoản của chính mình");
        }

        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public List<User> search(String keyword) {
        return userRepository.searchActiveUsers(keyword, null, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<User> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByDeletedFalse(pageable);
    }

    @Override
    public Page<User> searchActiveUsers(String keyword, Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String cleanKeyword = keyword == null ? null : keyword.trim();
        return userRepository.searchActiveUsers(cleanKeyword, role, pageable);
    }

    @Override
    @Transactional
    public void updateProfile(String username, String fullName, String phone, String email) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setFullName(fullName);
        user.setPhone(phone);
        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
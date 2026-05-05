package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.User;
import java.util.Optional;

public interface UserService {
    User registerNewUserAccount(User user);
    Optional<User> findByUsername(String username);
    void createAdminAccountIfNotExist();
}

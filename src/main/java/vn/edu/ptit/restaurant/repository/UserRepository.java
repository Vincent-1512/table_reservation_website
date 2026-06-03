package vn.edu.ptit.restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndDeletedFalse(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
    
    long countByDeletedFalse();

    List<User> findByDeletedFalse();

    Page<User> findByDeletedFalse(Pageable pageable);

    List<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username,
            String fullName,
            String email
    );

    @Query("""
            SELECT u
            FROM User u
            WHERE u.deleted = false
              AND (:role IS NULL OR u.role = :role)
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<User> searchActiveUsers(@Param("keyword") String keyword,
                                 @Param("role") Role role,
                                 Pageable pageable);
}
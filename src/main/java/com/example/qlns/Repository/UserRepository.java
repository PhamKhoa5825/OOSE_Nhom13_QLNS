package com.example.qlns.Repository;

import com.example.qlns.Entity.User;
import com.example.qlns.Enum.Role;
import com.example.qlns.Enum.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// =============================================
// TV2 - UserRepository
// =============================================
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByStatus(UserStatus status);
}

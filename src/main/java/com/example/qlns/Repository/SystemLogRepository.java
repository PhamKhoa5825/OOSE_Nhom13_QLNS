package com.example.qlns.Repository;

import com.example.qlns.Entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

// =============================================
// TV2 - SystemLogRepository
// =============================================
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    List<SystemLog> findByUserId(Long userId);
    List<SystemLog> findByAction(String action);
    List<SystemLog> findTop50ByOrderByCreatedAtDesc();

    // --- Thêm mới: phân trang + lọc nâng cao ---
    Page<SystemLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<SystemLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

    // Lọc theo khoảng thời gian
    Page<SystemLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Lọc theo user
    Page<SystemLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Xóa log cũ hơn X ngày (dọn dẹp)
    void deleteByCreatedAtBefore(LocalDateTime before);
}
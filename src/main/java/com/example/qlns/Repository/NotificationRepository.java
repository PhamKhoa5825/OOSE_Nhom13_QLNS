package com.example.qlns.Repository;

import com.example.qlns.Entity.Notification;
import com.example.qlns.Enum.NotificationTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// =============================================
// TV4 - NotificationRepository
// =============================================
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTargetType(NotificationTarget targetType);

    List<Notification> findByDepartmentId(Long departmentId);

    @Query("SELECT n FROM Notification n WHERE n.targetType = 'COMPANY' " +
            "OR (n.targetType = 'DEPARTMENT' AND n.department.id = :deptId)")
    List<Notification> findForEmployee(@Param("deptId") Long departmentId);
}

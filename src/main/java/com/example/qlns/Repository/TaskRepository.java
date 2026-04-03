package com.example.qlns.Repository;

import com.example.qlns.Entity.Task;
import com.example.qlns.Enum.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

// =============================================
// TV3 - TaskRepository
// =============================================
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToId(Long employeeId);

    List<Task> findByAssignedById(Long employeeId);

    List<Task> findByAssignedToIdAndStatus(Long employeeId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignedTo.department.id = :deptId")
    List<Task> findByDepartmentId(@Param("deptId") Long departmentId);

    @Query("SELECT t FROM Task t WHERE t.status != 'DONE' AND t.deadline < CURRENT_TIMESTAMP")
    List<Task> findOverdueTasks();

    // Tìm task cần đánh dấu OVERDUE
    // (deadline đã qua, chưa hoàn thành, chưa bị đánh dấu rồi)
    @Query("""
        SELECT t FROM Task t
        WHERE t.deadline < :now
          AND t.status IN ('PENDING', 'ACCEPTED')
    """)
    List<Task> findOverdueTasksToMark(@Param("now") LocalDateTime now);

    long countByAssignedToIdAndStatus(Long employeeId, TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.department.id = :deptId AND t.status = :status")
    long countByDepartmentAndStatus(@Param("deptId") Long departmentId, @Param("status") TaskStatus status);

    long countByStatus(TaskStatus status);

    long countByAssignedToId(Long employeeId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.department.id = :deptId")
    long countByDepartment(@Param("deptId") Long departmentId);
}

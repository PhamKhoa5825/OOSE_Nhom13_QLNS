package com.example.qlns.Repository;

import com.example.qlns.Entity.Request;
import com.example.qlns.Enum.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    // Tất cả đơn của một nhân viên (mới nhất trước)
    List<Request> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    // Lọc theo trạng thái
    List<Request> findByEmployeeIdAndStatusOrderByCreatedAtDesc(
            Long employeeId, RequestStatus status);

    // Đơn chờ duyệt của phòng ban (Manager xem)
    @Query("""
        SELECT r FROM Request r
        WHERE r.employee.department.id = :deptId
        ORDER BY r.createdAt DESC
    """)
    List<Request> findByDepartmentId(@Param("deptId") Long deptId);

    // Đơn chờ duyệt của phòng ban lọc theo status
    @Query("""
        SELECT r FROM Request r
        WHERE r.employee.department.id = :deptId
          AND r.status = :status
        ORDER BY r.createdAt DESC
    """)
    List<Request> findByDepartmentIdAndStatus(
            @Param("deptId") Long deptId,
            @Param("status") RequestStatus status);

    // Tất cả đơn toàn công ty (Admin xem)
    List<Request> findAllByOrderByCreatedAtDesc();

    // Đơn chờ duyệt toàn công ty
    List<Request> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    long countByStatus(RequestStatus status);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.employee.department.id = :deptId AND r.status = :status")
    long countByDepartmentIdAndStatus(@Param("deptId") Long deptId, @Param("status") RequestStatus status);

    long countByEmployeeIdAndStatus(Long employeeId, RequestStatus status);

    // Counts by keyword in title for categories
    @Query("SELECT COUNT(r) FROM Request r WHERE r.title LIKE %:keyword% AND r.employee.department.id = :deptId")
    long countByDepartmentAndKeyword(@Param("deptId") Long deptId, @Param("keyword") String keyword);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.title LIKE %:keyword%")
    long countByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.title LIKE %:keyword% AND r.employee.id = :empId")
    long countByEmployeeAndKeyword(@Param("empId") Long empId, @Param("keyword") String keyword);
}

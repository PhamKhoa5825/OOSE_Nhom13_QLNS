package com.example.qlns.Repository;

import com.example.qlns.Entity.*;
import com.example.qlns.Enum.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    List<Attendance> findByEmployeeId(Long employeeId);
    List<Attendance> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByDateAndEmployeeDepartmentId(LocalDate date, Long departmentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :empId " +
            "AND MONTH(a.date) = :month AND YEAR(a.date) = :year " +
            "AND a.status != 'ABSENT'")
    long countWorkingDays(@Param("empId") Long empId,
                          @Param("month") int month,
                          @Param("year") int year);

    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.department.id = :deptId " +
            "AND a.status = :status AND a.date BETWEEN :from AND :to")
    long countByDepartmentAndStatusBetween(@Param("deptId") Long deptId,
                                           @Param("status") AttendanceStatus status,
                                           @Param("from") LocalDate from,
                                           @Param("to") LocalDate to);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :empId " +
            "AND MONTH(a.date) = :month AND YEAR(a.date) = :year " +
            "AND a.status = :status")
    long countByEmployeeAndStatus(@Param("empId") Long empId,
                                  @Param("month") int month,
                                  @Param("year") int year,
                                  @Param("status") AttendanceStatus status);

    long countByEmployeeIdAndDateBetween(Long employeeId, LocalDate from, LocalDate to);
}

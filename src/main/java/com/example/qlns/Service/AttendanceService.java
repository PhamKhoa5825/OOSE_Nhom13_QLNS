package com.example.qlns.Service;

import com.example.qlns.DTO.Response.AttendanceDTO;
import com.example.qlns.DTO.Response.AttendanceStatsDTO;
import com.example.qlns.DTO.Response.EmployeeAttendanceStatsDTO;
import com.example.qlns.Entity.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceDTO checkIn(Long employeeId, Double lat, Double lng);
    AttendanceDTO checkOut(Long employeeId);
    List<Attendance> getByEmployee(Long empId);
    List<Attendance> getByDate(LocalDate date);
    List<Attendance> getByDateAndDepartment(LocalDate date, Long deptId);
    List<Attendance> getByEmployeeAndMonth(Long empId, int month, int year);
    AttendanceStatsDTO getMonthlyStats(Long empId, int month, int year);
    List<EmployeeAttendanceStatsDTO> getDepartmentStats(Long deptId, int month, int year);
    long countWorkingDays(Long empId, int month, int year);
}
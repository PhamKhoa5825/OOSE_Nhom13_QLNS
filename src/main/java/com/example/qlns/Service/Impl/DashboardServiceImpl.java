package com.example.qlns.Service.Impl;

import com.example.qlns.DTO.Response.*;
import com.example.qlns.Entity.*;
import com.example.qlns.Enum.*;
import com.example.qlns.Mapper.*;
import com.example.qlns.Repository.*;
import com.example.qlns.Service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository empRepo;
    private final AttendanceRepository attendanceRepo;
    private final TaskRepository taskRepo;
    private final RequestRepository requestRepo;
    private final UserRepository userRepo;
    private final com.example.qlns.Service.NotificationService notiService;
    
    private final EmployeeMapper empMapper;
    private final TaskMapper taskMapper;
    private final RequestMapper requestMapper;
    private final AttendanceMapper attendanceMapper;

    public DashboardServiceImpl(EmployeeRepository empRepo,
                                AttendanceRepository attendanceRepo,
                                TaskRepository taskRepo,
                                RequestRepository requestRepo,
                                UserRepository userRepo,
                                com.example.qlns.Service.NotificationService notiService,
                                EmployeeMapper empMapper,
                                TaskMapper taskMapper,
                                RequestMapper requestMapper,
                                AttendanceMapper attendanceMapper) {
        this.empRepo = empRepo;
        this.attendanceRepo = attendanceRepo;
        this.taskRepo = taskRepo;
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
        this.notiService = notiService;
        this.empMapper = empMapper;
        this.taskMapper = taskMapper;
        this.requestMapper = requestMapper;
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDashboardDTO getDashboard(Long deptId, int month, int year) {
        DepartmentDashboardDTO dto = new DepartmentDashboardDTO();

        long totalEmployees = empRepo.countByDepartmentIdAndStatus(deptId, EmployeeStatus.ACTIVE);
        dto.setTotalEmployees(totalEmployees);

        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        long onTimeCount = attendanceRepo.countByDepartmentAndStatusBetween(
                deptId, AttendanceStatus.ON_TIME, from, to);
        long lateCount = attendanceRepo.countByDepartmentAndStatusBetween(
                deptId, AttendanceStatus.LATE, from, to);

        long workingDaysInMonth = countWorkingDays(from, to);
        long expectedAttendance = workingDaysInMonth * totalEmployees;
        long actualAttendance = onTimeCount + lateCount;
        long absentCount = Math.max(0, expectedAttendance - actualAttendance);

        dto.setOnTimeCount(onTimeCount);
        dto.setLateCount(lateCount);
        dto.setAbsentCount(absentCount);

        if (expectedAttendance > 0) {
            dto.setOnTimeRate(Math.round(onTimeCount * 100.0 / expectedAttendance * 10) / 10.0);
            dto.setLateRate(Math.round(lateCount * 100.0 / expectedAttendance * 10) / 10.0);
            dto.setAbsentRate(Math.round(absentCount * 100.0 / expectedAttendance * 10) / 10.0);
        }

        long taskPending = taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.PENDING)
                         + taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.ACCEPTED);
        long taskCompleted = taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.DONE);
        long taskOverdue = taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.OVERDUE);

        dto.setTaskInProgress(taskPending);
        dto.setTaskPending(taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.PENDING));
        dto.setTaskCompleted(taskCompleted);
        dto.setTaskOverdue(taskOverdue);
        dto.setTaskTotal(taskPending + taskCompleted + taskOverdue);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDataDTO getDashboardData(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = user.getRole();
        
        Employee currentEmp = null;
        if (user.getEmployeeId() != null) {
            currentEmp = empRepo.findById(user.getEmployeeId()).orElse(null);
        }
        
        Long deptId = (currentEmp != null && currentEmp.getDepartment() != null) 
                      ? currentEmp.getDepartment().getId() : null;

        DashboardDataDTO dto = new DashboardDataDTO();
        LocalDate today = LocalDate.now();

        // 1. Stats
        if (role == Role.ADMIN) {
            dto.setTotalEmployees(empRepo.countByStatus(EmployeeStatus.ACTIVE));
            dto.setActiveTasksCount(taskRepo.countByStatus(TaskStatus.PENDING) + taskRepo.countByStatus(TaskStatus.ACCEPTED));
            dto.setOverdueTasksCount(taskRepo.countByStatus(TaskStatus.OVERDUE));
            
            // On-time rate global (last 30 days)
            LocalDate lastMonth = today.minusDays(30);
            long totalActive = empRepo.countByStatus(EmployeeStatus.ACTIVE);
            long onTime = attendanceRepo.findAll().stream()
                    .filter(a -> a.getDate().isAfter(lastMonth) && a.getStatus() == AttendanceStatus.ON_TIME)
                    .count();
            long expected = Math.max(1, totalActive * 22); 
            dto.setOnTimeRate(Math.round(((double) onTime * 100 / expected) * 10) / 10.0);
        } else if (role == Role.MANAGER && deptId != null) {
            dto.setTotalEmployees(empRepo.countByDepartmentIdAndStatus(deptId, EmployeeStatus.ACTIVE));
            dto.setActiveTasksCount(taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.PENDING) + taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.ACCEPTED));
            dto.setOverdueTasksCount(taskRepo.countByDepartmentAndStatus(deptId, TaskStatus.OVERDUE));
            
            DepartmentDashboardDTO stats = getDashboard(deptId, today.getMonthValue(), today.getYear());
            dto.setOnTimeRate(stats.getOnTimeRate());
        } else if (currentEmp != null) {
            // Employee view (Self focused)
            dto.setTotalEmployees(0); 
            dto.setActiveTasksCount(taskRepo.countByAssignedToIdAndStatus(currentEmp.getId(), TaskStatus.PENDING) 
                                   + taskRepo.countByAssignedToIdAndStatus(currentEmp.getId(), TaskStatus.ACCEPTED));
            dto.setOverdueTasksCount(taskRepo.countByAssignedToIdAndStatus(currentEmp.getId(), TaskStatus.OVERDUE));
            dto.setOnTimeRate(0); 
        }

        // 2. Recent Employees
        if (role == Role.ADMIN) {
            dto.setRecentEmployees(empRepo.findAll(PageRequest.of(0, 5, Sort.by("id").descending())).getContent()
                    .stream().map(empMapper::toDTO).collect(Collectors.toList()));
        } else if (deptId != null) {
            dto.setRecentEmployees(empRepo.findByDepartmentIdAndStatus(deptId, EmployeeStatus.ACTIVE)
                    .stream().limit(5).map(empMapper::toDTO).collect(Collectors.toList()));
        }

        // 3. Today Attendance
        List<Attendance> todayAtt;
        if (role == Role.ADMIN) {
            todayAtt = attendanceRepo.findByDate(today);
        } else if (deptId != null) {
            todayAtt = attendanceRepo.findByDateAndEmployeeDepartmentId(today, deptId);
        } else if (currentEmp != null) {
            todayAtt = attendanceRepo.findByEmployeeIdAndDate(currentEmp.getId(), today).map(List::of).orElse(List.of());
        } else {
            todayAtt = List.of();
        }
        dto.setTodayAttendance(todayAtt.stream().map(attendanceMapper::toDTO).collect(Collectors.toList()));

        // 4. Pending Requests
        List<Request> pending;
        if (role == Role.ADMIN) {
            pending = requestRepo.findByStatusOrderByCreatedAtDesc(RequestStatus.PENDING);
        } else if (role == Role.MANAGER && deptId != null) {
            pending = requestRepo.findByDepartmentIdAndStatus(deptId, RequestStatus.PENDING);
        } else if (currentEmp != null) {
            pending = requestRepo.findByEmployeeIdAndStatusOrderByCreatedAtDesc(currentEmp.getId(), RequestStatus.PENDING);
        } else {
            pending = List.of();
        }
        dto.setPendingRequests(pending.stream().limit(5).map(requestMapper::toDTO).collect(Collectors.toList()));

        // 5. Tasks (Kanban)
        List<Task> tasks;
        if (role == Role.ADMIN) {
            tasks = taskRepo.findAll();
        } else if (deptId != null) {
            tasks = taskRepo.findByDepartmentId(deptId);
        } else if (currentEmp != null) {
            tasks = taskRepo.findByAssignedToId(currentEmp.getId());
        } else {
            tasks = List.of();
        }
        dto.setKanbanTasks(tasks.stream().limit(10).map(taskMapper::toDTO).collect(Collectors.toList()));
        
        Map<String, Long> statusCounts = new HashMap<>();
        for (TaskStatus s : TaskStatus.values()) {
            statusCounts.put(s.name(), tasks.stream().filter(t -> t.getStatus() == s).count());
        }
        dto.setTaskStatusCounts(statusCounts);

        // 6. Notifications
        dto.setNotifications(notiService.getForEmployee(deptId, userId).stream().limit(3).collect(Collectors.toList()));

        // 7. Weekly Chart (Last 5 days)
        List<String> days = new ArrayList<>();
        List<Double> rates = new ArrayList<>();
        for (int i = 4; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            days.add(date.getDayOfWeek().name().substring(0, 3));
            long present = attendanceRepo.findByDate(date).size();
            long total = Math.max(1, empRepo.countByStatus(EmployeeStatus.ACTIVE));
            rates.add((double) present * 100 / total);
        }
        dto.setWeeklyAttendanceDays(days);
        dto.setWeeklyAttendanceRates(rates);

        return dto;
    }

    private long countWorkingDays(LocalDate from, LocalDate to) {
        long count = 0;
        LocalDate date = from;
        while (!date.isAfter(to)) {
            if (date.getDayOfWeek().getValue() <= 5) {
                count++;
            }
            date = date.plusDays(1);
        }
        return count;
    }
}

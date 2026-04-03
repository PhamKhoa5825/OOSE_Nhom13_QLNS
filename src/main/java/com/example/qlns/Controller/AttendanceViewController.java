package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.AttendanceDTO;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.AttendanceStatus;
import com.example.qlns.Enum.Role;
import com.example.qlns.Mapper.AttendanceMapper;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.AttendanceService;
import com.example.qlns.Service.EmployeeQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AttendanceViewController {

    private final AttendanceService attendanceService;
    private final SecurityService securityService;
    private final UserRepository userRepo;
    private final EmployeeQueryService employeeService;
    private final AttendanceMapper attendanceMapper;

    public AttendanceViewController(AttendanceService attendanceService,
                                   SecurityService securityService,
                                   UserRepository userRepo,
                                    EmployeeQueryService employeeService,
                                   AttendanceMapper attendanceMapper) {
        this.attendanceService = attendanceService;
        this.securityService = securityService;
        this.userRepo = userRepo;
        this.employeeService = employeeService;
        this.attendanceMapper = attendanceMapper;
    }

    @GetMapping("/attendance")
    public String attendance(Model model) {
        Long userId = securityService.getCurrentUserId();
        User user = userRepo.findById(userId).orElseThrow();
        Role role = user.getRole();
        Long empId = user.getEmployeeId();
        LocalDate today = LocalDate.now();

        List<com.example.qlns.Entity.Attendance> rawAttendances;
        if (role == Role.ADMIN) {
            rawAttendances = attendanceService.getByDate(today);
        } else if (role == Role.MANAGER) {
            Long deptId = securityService.getManagerDepartmentId();
            rawAttendances = attendanceService.getByDateAndDepartment(today, deptId);
        } else {
            rawAttendances = attendanceService.getByEmployee(empId);
        }

        List<AttendanceDTO> attendances = rawAttendances.stream()
                .map(attendanceMapper::toDTO)
                .collect(Collectors.toList());

        // Stats calculation
        long present = rawAttendances.stream().filter(a -> a.getDate().equals(today)).count();
        long onTime = rawAttendances.stream().filter(a -> a.getDate().equals(today) && a.getStatus() == AttendanceStatus.ON_TIME).count();
        long late = rawAttendances.stream().filter(a -> a.getDate().equals(today) && a.getStatus() == AttendanceStatus.LATE).count();
        
        long totalEmployees;
        if (role == Role.ADMIN) {
            totalEmployees = employeeService.getAll().size();
        } else if (role == Role.MANAGER) {
            totalEmployees = employeeService.getByDepartment(securityService.getManagerDepartmentId()).size();
        } else {
            totalEmployees = 1;
        }
        long absent = Math.max(0, totalEmployees - present);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPresent", present);
        stats.put("onTime", onTime);
        stats.put("late", late);
        stats.put("absent", absent);
        stats.put("totalEmployees", totalEmployees);
        stats.put("onTimeRate", present > 0 ? (onTime * 100.0 / present) : 0);

        model.addAttribute("attendances", attendances);
        model.addAttribute("stats", stats);
        model.addAttribute("activePage", "attendance");
        model.addAttribute("today", today);
        model.addAttribute("currentUserEmpId", empId);
        
        // Check if user already checked in today
        boolean hasCheckedIn = rawAttendances.stream().anyMatch(a -> a.getDate().equals(today) && a.getEmployee().getId().equals(empId));
        model.addAttribute("hasCheckedIn", hasCheckedIn);

        return "attendance";
    }
}

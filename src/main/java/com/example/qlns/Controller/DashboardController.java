package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.DepartmentDashboardDTO;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Dashboard Controller
 * Provides department-level statistics for Manager/Admin.
 */
@RestController
@RequestMapping("/api/departments")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityService securityService;

    public DashboardController(DashboardService dashboardService, SecurityService securityService) {
        this.dashboardService = dashboardService;
        this.securityService = securityService;
    }

    /**
     * GET /api/departments/{deptId}/dashboard?month=3&year=2026
     * Returns attendance + task + employee statistics for the department.
     */
    @GetMapping("/{deptId}/dashboard")
    public ResponseEntity<DepartmentDashboardDTO> getDashboard(
            @PathVariable("deptId") Long deptId,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year) {
        
        if (month == null) month = java.time.LocalDate.now().getMonthValue();
        if (year == null) year = java.time.LocalDate.now().getYear();

        // Manager chỉ xem dashboard phòng ban mình, Admin xem tất cả
        securityService.validateManagerDepartment(deptId);
        return ResponseEntity.ok(dashboardService.getDashboard(deptId, month, year));
    }
}

package com.example.qlns.Service;

import com.example.qlns.DTO.Response.DashboardDataDTO;
import com.example.qlns.DTO.Response.DepartmentDashboardDTO;

public interface DashboardService {
    DepartmentDashboardDTO getDashboard(Long deptId, int month, int year);
    DashboardDataDTO getDashboardData(Long userId);
}

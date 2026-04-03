package com.example.qlns.Service;

import com.example.qlns.DTO.Response.TaskStatsDTO;

/**
 * ISP: Interface chỉ chứa thao tác thống kê Task.
 * Được sử dụng bởi: DashboardServiceImpl, HomeViewController
 * Các client này chỉ cần thống kê, không cần CRUD hay scheduling.
 */
public interface TaskStatsService {
    TaskStatsDTO getTaskStats(Long userId);
}

package com.example.qlns.Service;

/**
 * ISP: Interface chỉ chứa tác vụ lên lịch/tự động cho Task.
 * Được sử dụng bởi: Spring Scheduler (chạy tự động mỗi sáng 8h)
 * Scheduler chỉ cần method này, không cần biết về CRUD hay Stats.
 */
public interface TaskSchedulingService {
    void markOverdueTasks();
}

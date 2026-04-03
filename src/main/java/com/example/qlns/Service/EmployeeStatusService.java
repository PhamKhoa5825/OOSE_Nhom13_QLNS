package com.example.qlns.Service;

/**
 * ISP: Interface chỉ chứa các thao tác thay đổi trạng thái Employee/User.
 * Được sử dụng bởi: EmployeeController (resign, reactivate, approve, updateStatus)
 * Chỉ Admin mới gọi các method này.
 */
public interface EmployeeStatusService {
    void resign(Long id);
    void reactivate(Long id);
    void approveEmployee(Long id, Long deptId, String role);
    void updateUserStatus(Long id, String status);
}

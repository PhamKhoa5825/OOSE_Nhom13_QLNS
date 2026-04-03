package com.example.qlns.Security;

import com.example.qlns.Entity.Department;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.Role;
import com.example.qlns.Exception.ForbiddenException;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Repository.DepartmentRepository;
import com.example.qlns.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Utility service for authorization checks.
 * Provides methods to validate that a Manager only accesses
 * data belonging to their own department. Admin bypasses all checks.
 */
@Service
public class SecurityService {

    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final com.example.qlns.Repository.EmployeeRepository empRepo;

    public SecurityService(UserRepository userRepo, DepartmentRepository deptRepo, com.example.qlns.Repository.EmployeeRepository empRepo) {
        this.userRepo = userRepo;
        this.deptRepo = deptRepo;
        this.empRepo = empRepo;
    }

    /**
     * Get the current authenticated user's ID from SecurityContext.
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            throw new ForbiddenException("Không tìm thấy thông tin xác thực");
        }
        return ((UserDetailsImpl) auth.getPrincipal()).getUserId();
    }

    /**
     * Get the current authenticated user's role.
     */
    public Role getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            throw new ForbiddenException("Không tìm thấy thông tin xác thực");
        }
        return ((UserDetailsImpl) auth.getPrincipal()).getRole();
    }

    /**
     * Check if the current user is an Admin.
     */
    public boolean isAdmin() {
        return getCurrentUserRole() == Role.ADMIN;
    }

    /**
     * Get the department ID that the current Manager is managing.
     * Chain: userId → User.employeeId → Department where manager_id = employeeId.
     *
     * @return the department ID
     * @throws ResourceNotFoundException if the manager is not assigned to any department
     */
    public Long getManagerDepartmentId() {
        Long userId = getCurrentUserId();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ForbiddenException("Không tìm thấy thông tin User"));

        Long employeeId = user.getEmployeeId();
        if (employeeId == null) {
            throw new ForbiddenException("Tài khoản chưa được liên kết với hồ sơ nhân viên");
        }

        // 1. Kiểm tra nếu là Manager chính thức của một phòng ban
        java.util.Optional<Department> managedDept = deptRepo.findByManagerId(employeeId);
        if (managedDept.isPresent()) {
            return managedDept.get().getId();
        }

        // 2. Nếu không phải Manager, vẫn trả về ID phòng ban mà nhân viên này thuộc về
        Long deptId = empRepo.findDepartmentIdByEmployeeId(employeeId);
        if (deptId != null) {
            return deptId;
        }

        throw new ForbiddenException("Bạn không thuộc phòng ban nào");
    }

    /**
     * Validate that the given deptId belongs to the current Manager.
     * Admin can access any department.
     * Throws ForbiddenException if Manager tries to access another department.
     *
     * @param deptId department ID to validate
     */
    public void validateManagerDepartment(Long deptId) {
        // Admin bypasses all department checks
        if (isAdmin()) return;

        Long managerDeptId = getManagerDepartmentId();
        if (!managerDeptId.equals(deptId)) {
            throw new ForbiddenException(
                    "Bạn không có quyền truy cập dữ liệu phòng ban này");
        }
    }
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }
}

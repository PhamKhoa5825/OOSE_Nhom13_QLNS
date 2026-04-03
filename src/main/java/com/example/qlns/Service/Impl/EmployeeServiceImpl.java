package com.example.qlns.Service.Impl;

import com.example.qlns.DTO.Response.EmployeeDTO;
import com.example.qlns.DTO.Response.EmployeeDetailDTO;
import com.example.qlns.DTO.Response.EmployeeSummaryDTO;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.EmployeeStatus;
import com.example.qlns.Enum.Role;
import com.example.qlns.Enum.UserStatus;
import com.example.qlns.Exception.DuplicateException;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Mapper.EmployeeMapper;
import com.example.qlns.Repository.DepartmentRepository;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Service.EmployeeCrudService;
import com.example.qlns.Service.EmployeeQueryService;
import com.example.qlns.Service.EmployeeStatusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ISP Refactored: Một class implement 3 interface nhỏ thay vì 1 interface lớn.
 * 
 * Trước: implements EmployeeService (16 methods trong 1 interface)
 * Sau:   implements EmployeeCrudService (3 methods)
 *                 + EmployeeQueryService (9 methods)
 *                 + EmployeeStatusService (4 methods)
 * 
 * Lợi ích:
 * - Controller chỉ inject interface mình cần (ví dụ: DashboardController chỉ cần EmployeeQueryService)
 * - Dễ mock trong unit test (chỉ mock interface nhỏ thay vì 16 methods)
 * - Khi thêm method mới, chỉ ảnh hưởng interface liên quan
 */
@Service
public class EmployeeServiceImpl implements EmployeeCrudService, EmployeeQueryService, EmployeeStatusService {

    private final EmployeeRepository empRepo;
    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository empRepo, UserRepository userRepo,
                               DepartmentRepository deptRepo, EmployeeMapper employeeMapper) {
        this.empRepo = empRepo;
        this.userRepo = userRepo;
        this.deptRepo = deptRepo;
        this.employeeMapper = employeeMapper;
    }

    // ════════════════════════════════════════════════════════════
    // EmployeeCrudService — CRUD cơ bản
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public Employee getById(Long id) {
        return empRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên id=" + id));
    }

    @Override
    @Transactional
    public Employee create(Employee emp, String email, String password, Role role) {
        if (userRepo.existsByEmail(email))
            throw new DuplicateException("Email đã được sử dụng: " + email);

        User user = new User(email.split("@")[0], email, password, role);
        user = userRepo.save(user);

        emp.setEmail(email);
        Employee saved = empRepo.save(emp);

        user.setEmployeeId(saved.getId());
        userRepo.save(user);

        return saved;
    }

    @Override
    @Transactional
    public Employee update(Long id, Employee req) {
        Employee emp = getById(id);
        if (req.getFullName() != null) emp.setFullName(req.getFullName());
        if (req.getPhone() != null) emp.setPhone(req.getPhone());
        if (req.getAddress() != null) emp.setAddress(req.getAddress());
        if (req.getPosition() != null) emp.setPosition(req.getPosition());
        if (req.getDepartment() != null) emp.setDepartment(req.getDepartment());
        if (req.getAvatarUrl() != null) emp.setAvatarUrl(req.getAvatarUrl());
        if (req.getGender() != null) emp.setGender(req.getGender());
        if (req.getDateOfBirth() != null) emp.setDateOfBirth(req.getDateOfBirth());
        return empRepo.save(emp);
    }

    // ════════════════════════════════════════════════════════════
    // EmployeeQueryService — Truy vấn & tìm kiếm
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> getAll(Pageable pageable) {
        return empRepo.findAllWithDept(pageable).map(emp -> {
            User user = userRepo.findByEmail(emp.getEmail()).orElse(null);
            String role = (user != null) ? user.getRole().name() : "EMPLOYEE";
            String userStatus = (user != null) ? user.getStatus().name() : "INACTIVE";
            return employeeMapper.toDTO(emp, role, userStatus);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAll() {
        return empRepo.findAllWithDept().stream()
                .map(emp -> {
                    User user = userRepo.findByEmail(emp.getEmail()).orElse(null);
                    String role = (user != null) ? user.getRole().name() : "EMPLOYEE";
                    String userStatus = (user != null) ? user.getStatus().name() : "INACTIVE";
                    return employeeMapper.toDTO(emp, role, userStatus);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> getByDepartment(Long deptId, Pageable pageable) {
        return empRepo.findByDepartmentIdAndStatus(deptId, EmployeeStatus.ACTIVE, pageable)
                .map(emp -> {
                    User user = userRepo.findByEmail(emp.getEmail()).orElse(null);
                    String role = (user != null) ? user.getRole().name() : "EMPLOYEE";
                    String userStatus = (user != null) ? user.getStatus().name() : "INACTIVE";
                    return employeeMapper.toDTO(emp, role, userStatus);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getByDepartment(Long deptId) {
        return empRepo.findByDepartmentIdAndStatus(deptId, EmployeeStatus.ACTIVE).stream()
                .map(emp -> {
                    User user = userRepo.findByEmail(emp.getEmail()).orElse(null);
                    String role = (user != null) ? user.getRole().name() : "EMPLOYEE";
                    String userStatus = (user != null) ? user.getStatus().name() : "INACTIVE";
                    return employeeMapper.toDTO(emp, role, userStatus);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> search(String keyword, Long deptId, Pageable pageable) {
        return empRepo.search(keyword, deptId, pageable).map(emp -> {
            User user = userRepo.findByEmail(emp.getEmail()).orElse(null);
            String role = (user != null) ? user.getRole().name() : "EMPLOYEE";
            String userStatus = uStatus(emp.getEmail());
            return employeeMapper.toDTO(emp, role, userStatus);
        });
    }

    @Override
    public List<Employee> search(String keyword, Long deptId) {
        return empRepo.search(keyword, deptId);
    }

    @Override
    public String getRoleByEmployeeId(Long empId) {
        Employee emp = getById(empId);
        return userRepo.findByEmail(emp.getEmail())
                .map(u -> u.getRole().name())
                .orElse("EMPLOYEE");
    }

    @Override
    public EmployeeSummaryDTO getEmployeeSummary(Long id) {
        return employeeMapper.toSummaryDTO(getById(id));
    }

    @Override
    public EmployeeDetailDTO getEmployeeDetail(Long id) {
        Employee emp = getById(id);
        String role = userRepo.findByEmail(emp.getEmail())
                .map(u -> u.getRole().name())
                .orElse("EMPLOYEE");
        return employeeMapper.toDetailDTO(emp, role);
    }

    // ════════════════════════════════════════════════════════════
    // EmployeeStatusService — Quản lý trạng thái (Admin only)
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void resign(Long id) {
        Employee emp = getById(id);
        emp.setStatus(EmployeeStatus.RESIGNED);
        empRepo.save(emp);
        userRepo.findByEmail(emp.getEmail()).ifPresent(u -> {
            u.setStatus(UserStatus.INACTIVE);
            userRepo.save(u);
        });
    }

    @Override
    @Transactional
    public void reactivate(Long id) {
        Employee emp = getById(id);
        emp.setStatus(EmployeeStatus.ACTIVE);
        empRepo.save(emp);
        userRepo.findByEmail(emp.getEmail()).ifPresent(u -> {
            u.setStatus(UserStatus.ACTIVE);
            userRepo.save(u);
        });
    }

    @Override
    @Transactional
    public void approveEmployee(Long id, Long deptId, String role) {
        Employee emp = getById(id);
        com.example.qlns.Entity.Department dept = deptRepo.findById(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban id=" + deptId));

        emp.setDepartment(dept);
        emp.setStatus(EmployeeStatus.ACTIVE);
        empRepo.save(emp);

        userRepo.findByEmail(emp.getEmail()).ifPresent(u -> {
            u.setStatus(UserStatus.ACTIVE);
            u.setRole(Role.valueOf(role));
            userRepo.save(u);
        });
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, String status) {
        Employee emp = getById(id);
        userRepo.findByEmail(emp.getEmail()).ifPresent(u -> {
            u.setStatus(UserStatus.valueOf(status));
            userRepo.save(u);
        });
    }

    // ════════════════════════════════════════════════════════════
    // Private helpers
    // ════════════════════════════════════════════════════════════

    private String uStatus(String email) {
        return userRepo.findByEmail(email).map(u -> u.getStatus().name()).orElse("INACTIVE");
    }
}

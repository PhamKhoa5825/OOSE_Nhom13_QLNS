package com.example.qlns.Controller;

import com.example.qlns.DTO.Request.CreateEmployeeRequest;
import com.example.qlns.DTO.Request.UpdateEmployeeRequest;
import com.example.qlns.DTO.Response.EmployeeDTO;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.Gender;
import com.example.qlns.Enum.Role;
import com.example.qlns.Mapper.EmployeeMapper;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.EmployeeCrudService;
import com.example.qlns.Service.EmployeeQueryService;
import com.example.qlns.Service.EmployeeStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ISP Refactored Controller.
 * 
 * TRƯỚC (1 interface lớn):
 *   private final EmployeeService empService;  // 16 methods, dùng hết 12
 * 
 * SAU (3 interface nhỏ, inject đúng cái cần):
 *   private final EmployeeCrudService crudService;      // create, update, getById
 *   private final EmployeeQueryService queryService;    // getAll, search, getByDepartment
 *   private final EmployeeStatusService statusService;  // resign, reactivate, approve
 * 
 * → Mỗi dependency rõ ràng: đọc constructor biết ngay Controller làm gì.
 * → Unit test: mock 3 interface nhỏ thay vì 1 interface 16 methods.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeCrudService crudService;
    private final EmployeeQueryService queryService;
    private final EmployeeStatusService statusService;
    private final SecurityService securityService;
    private final UserRepository userRepo;
    private final EmployeeMapper employeeMapper;

    public EmployeeController(EmployeeCrudService crudService,
                              EmployeeQueryService queryService,
                              EmployeeStatusService statusService,
                              SecurityService securityService,
                              UserRepository userRepo,
                              EmployeeMapper employeeMapper) {
        this.crudService = crudService;
        this.queryService = queryService;
        this.statusService = statusService;
        this.securityService = securityService;
        this.userRepo = userRepo;
        this.employeeMapper = employeeMapper;
    }

    // ── Query methods → dùng EmployeeQueryService ─────────────

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAll() {
        if (securityService.isAdmin()) {
            return ResponseEntity.ok(queryService.getAll());
        }
        Long deptId = securityService.getManagerDepartmentId();
        return ResponseEntity.ok(queryService.getByDepartment(deptId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable("id") Long id) {
        Employee emp = crudService.getById(id);
        User user = userRepo.findByEmail(emp.getEmail()).orElse(null);
        String role = (user != null) ? user.getRole().name() : "EMPLOYEE";
        String userStatus = (user != null) ? user.getStatus().name() : "INACTIVE";
        return ResponseEntity.ok(employeeMapper.toDTO(emp, role, userStatus));
    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<EmployeeDTO>> getByDepartment(@PathVariable("deptId") Long deptId) {
        securityService.validateManagerDepartment(deptId);
        return ResponseEntity.ok(queryService.getByDepartment(deptId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDTO>> search(@RequestParam("keyword") String keyword) {
        Long deptId = securityService.isAdmin() ? null : securityService.getManagerDepartmentId();
        return ResponseEntity.ok(queryService.search(keyword, deptId).stream()
                .map(e -> {
                    User user = userRepo.findByEmail(e.getEmail()).orElse(null);
                    String role = (user != null) ? user.getRole().name() : "EMPLOYEE";
                    String status = (user != null) ? user.getStatus().name() : "INACTIVE";
                    return employeeMapper.toDTO(e, role, status);
                })
                .collect(Collectors.toList()));
    }

    // ── CRUD methods → dùng EmployeeCrudService ───────────────

    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@RequestBody CreateEmployeeRequest req) {
        Employee emp = new Employee();
        emp.setFullName(req.getFullName());
        emp.setPhone(req.getPhone());
        emp.setAddress(req.getAddress());
        emp.setPosition(req.getPosition());
        if (req.getJoinDate() != null) emp.setJoinDate(LocalDate.parse(req.getJoinDate()));
        if (req.getDateOfBirth() != null) emp.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
        if (req.getGender() != null) emp.setGender(Gender.valueOf(req.getGender()));

        Role role = Role.EMPLOYEE;
        if (req.getRole() != null && securityService.isAdmin()) {
            try { role = Role.valueOf(req.getRole()); } catch (IllegalArgumentException ignored) {}
        }

        Employee saved = crudService.create(emp, req.getEmail(), req.getPassword(), role);
        return ResponseEntity.ok(employeeMapper.toDTO(saved, role.name(), "ACTIVE"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable("id") Long id,
                                              @RequestBody UpdateEmployeeRequest req) {
        if (!securityService.isAdmin()) return ResponseEntity.status(403).build();
        Employee updateReq = new Employee();
        updateReq.setFullName(req.getFullName());
        updateReq.setPhone(req.getPhone());
        updateReq.setAddress(req.getAddress());
        updateReq.setPosition(req.getPosition());
        updateReq.setAvatarUrl(req.getAvatarUrl());
        Employee updated = crudService.update(id, updateReq);

        User user = userRepo.findByEmail(updated.getEmail()).orElse(null);
        String userRole = (user != null) ? user.getRole().name() : "EMPLOYEE";
        String status = (user != null) ? user.getStatus().name() : "ACTIVE";
        return ResponseEntity.ok(employeeMapper.toDTO(updated, userRole, status));
    }

    // ── Status methods → dùng EmployeeStatusService ───────────

    @PutMapping("/{id}/resign")
    public ResponseEntity<Void> resign(@PathVariable("id") Long id) {
        if (!securityService.isAdmin()) return ResponseEntity.status(403).build();
        statusService.resign(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable("id") Long id) {
        if (!securityService.isAdmin()) return ResponseEntity.status(403).build();
        statusService.reactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable("id") Long id,
                                        @RequestParam("deptId") Long deptId,
                                        @RequestParam("role") String role) {
        if (!securityService.isAdmin()) return ResponseEntity.status(403).build();
        statusService.approveEmployee(id, deptId, role);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id,
                                             @RequestParam("status") String status) {
        if (!securityService.isAdmin()) return ResponseEntity.status(403).build();
        statusService.updateUserStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}

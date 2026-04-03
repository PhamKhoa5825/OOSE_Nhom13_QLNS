package com.example.qlns.Controller;

import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.SystemLog;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Repository.SystemLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/logs")
public class SystemLogController {

    private final SystemLogRepository logRepo;
    private final EmployeeRepository employeeRepo;

    public SystemLogController(SystemLogRepository logRepo, EmployeeRepository employeeRepo) {
        this.logRepo = logRepo;
        this.employeeRepo = employeeRepo;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getRecentLogs() {
        List<Map<String, Object>> result = logRepo.findTop50ByOrderByCreatedAtDesc()
                .stream().map(this::toMap).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/filter")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> filterByAction(@RequestParam String action) {
        List<Map<String, Object>> result = logRepo.findByAction(action)
                .stream().map(this::toMap).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/paged")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPagedLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<SystemLog> logPage = logRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        return ResponseEntity.ok(toPageResponse(logPage));
    }

    @GetMapping("/search")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> searchLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SystemLog> logPage;

        if (userId != null) {
            logPage = logRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        } else if (fromDate != null && toDate != null) {
            LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
            LocalDateTime to = LocalDate.parse(toDate).atTime(23, 59, 59);
            logPage = logRepo.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to, pageable);
        } else if (action != null && !action.isEmpty()) {
            logPage = logRepo.findByActionOrderByCreatedAtDesc(action, pageable);
        } else {
            logPage = logRepo.findAllByOrderByCreatedAtDesc(pageable);
        }

        return ResponseEntity.ok(toPageResponse(logPage));
    }

    private Map<String, Object> toPageResponse(Page<SystemLog> logPage) {
        Map<String, Object> response = new HashMap<>();
        response.put("logs", logPage.getContent().stream().map(this::toMap).collect(Collectors.toList()));
        response.put("currentPage", logPage.getNumber());
        response.put("totalPages", logPage.getTotalPages());
        response.put("totalItems", logPage.getTotalElements());
        return response;
    }

    private Map<String, Object> toMap(SystemLog log) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", log.getId());
        map.put("action", log.getAction());
        map.put("description", log.getDescription());
        map.put("createdAt", log.getCreatedAt() != null ? log.getCreatedAt().toString() : null);

        String username = "System";
        String department = null;
        String position = null;

        if (log.getUser() != null) {
            username = log.getUser().getUsername();
            Long empId = log.getUser().getEmployeeId();
            if (empId != null) {
                Employee emp = employeeRepo.findById(empId).orElse(null);
                if (emp != null) {
                    position = emp.getPosition();
                    if (emp.getDepartment() != null) {
                        department = emp.getDepartment().getName();
                    }
                }
            }
        }

        map.put("username", username);
        map.put("department", department);
        map.put("position", position);
        return map;
    }
}
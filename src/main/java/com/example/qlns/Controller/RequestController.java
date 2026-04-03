package com.example.qlns.Controller;

import com.example.qlns.DTO.Request.*;
import com.example.qlns.DTO.Response.*;
import com.example.qlns.Entity.User;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final SecurityService securityService;
    private final UserRepository userRepo;

    public RequestController(RequestService requestService, SecurityService securityService, UserRepository userRepo) {
        this.requestService = requestService;
        this.securityService = securityService;
        this.userRepo = userRepo;
    }

    // ── Nhân viên: xem đơn của mình ──────────────────────────
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<RequestDTO>> getMyRequests(
            @PathVariable("empId") Long empId) {
        return ResponseEntity.ok(requestService.getMyRequests(empId));
    }

    // ── Nhân viên: tạo đơn ───────────────────────────────────
    @PostMapping("/employee/{empId}")
    public ResponseEntity<RequestDTO> createRequest(
            @PathVariable("empId") Long empId,
            @RequestBody CreateRequestRequest req) {
        return ResponseEntity.ok(requestService.createRequest(empId, req));
    }

    // ── Nhân viên: sửa đơn ───────────────────────────────────
    @PutMapping("/{id}/employee/{empId}")
    public ResponseEntity<RequestDTO> updateRequest(
            @PathVariable("id") Long id,
            @PathVariable("empId") Long empId,
            @RequestBody CreateRequestRequest req) {
        return ResponseEntity.ok(requestService.updateRequest(id, empId, req));
    }

    // ── Nhân viên: huỷ đơn ───────────────────────────────────
    @DeleteMapping("/{id}/employee/{empId}")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable("id") Long id,
            @PathVariable("empId") Long empId) {
        requestService.cancelRequest(id, empId);
        return ResponseEntity.noContent().build();
    }

    // ── Manager: xem tất cả đơn phòng ban ────────────────────
    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<RequestDTO>> getByDepartment(
            @PathVariable("deptId") Long deptId) {
        securityService.validateManagerDepartment(deptId);
        return ResponseEntity.ok(requestService.getByDepartment(deptId));
    }

    // ── Manager: lọc đơn phòng ban theo trạng thái ──────────
    @GetMapping("/department/{deptId}/status")
    public ResponseEntity<List<RequestDTO>> getByDepartmentAndStatus(
            @PathVariable("deptId") Long deptId,
            @RequestParam("status") String status) {
        securityService.validateManagerDepartment(deptId);
        return ResponseEntity.ok(requestService.getByDepartmentAndStatus(deptId, status));
    }

    // ── Manager: đơn chờ duyệt phòng ban ─────────────────────
    @GetMapping("/department/{deptId}/pending")
    public ResponseEntity<List<RequestDTO>> getPendingByDepartment(
            @PathVariable("deptId") Long deptId) {
        securityService.validateManagerDepartment(deptId);
        return ResponseEntity.ok(requestService.getPendingByDepartment(deptId));
    }

    // ── Xem chi tiết đơn ──────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<RequestDTO> getRequestById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    // ── Manager/Admin: duyệt hoặc từ chối ────────────────────
    @PutMapping("/{id}/review/employee/{reviewerId}")
    public ResponseEntity<RequestDTO> reviewRequest(
            @PathVariable("id") Long id,
            @PathVariable("reviewerId") Long reviewerId,
            @RequestBody ReviewRequestRequest req) {
        return ResponseEntity.ok(requestService.reviewRequest(id, reviewerId, req));
    }

    // ── Endpoint để App gọi đơn giản (Dùng trong RequestActivity.java) ──
    @PutMapping("/{id}/status")
    public ResponseEntity<RequestDTO> updateRequestStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {
        Long reviewerUserId = securityService.getCurrentUserId();
        User user = userRepo.findById(reviewerUserId).orElseThrow();
        Long reviewerId = user.getEmployeeId();

        ReviewRequestRequest reviewRequest = new ReviewRequestRequest();
        reviewRequest.setApproved("APPROVED".equalsIgnoreCase(status));
        if (!reviewRequest.isApproved()) {
            reviewRequest.setRejectionReason("Từ chối bởi quản lý");
        }

        return ResponseEntity.ok(requestService.reviewRequest(id, reviewerId, reviewRequest));
    }

    // ── Admin: xem tất cả đơn ────────────────────────────────
    @GetMapping
    public ResponseEntity<List<RequestDTO>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    // ── Admin: lọc tất cả đơn theo trạng thái ──────────────
    @GetMapping("/status")
    public ResponseEntity<List<RequestDTO>> getAllRequestsByStatus(@RequestParam("status") String status) {
        return ResponseEntity.ok(requestService.getAllRequestsByStatus(status));
    }
}
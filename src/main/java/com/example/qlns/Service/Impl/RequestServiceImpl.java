package com.example.qlns.Service.Impl;

import com.example.qlns.DTO.Request.CreateRequestRequest;
import com.example.qlns.DTO.Request.ReviewRequestRequest;
import com.example.qlns.DTO.Response.RequestDTO;
import com.example.qlns.DTO.Response.RequestStatsDTO;
import com.example.qlns.Entity.Department;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.Request;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.RequestStatus;
import com.example.qlns.Enum.Role;
import com.example.qlns.Exception.BadRequestException;
import com.example.qlns.Exception.ForbiddenException;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Mapper.RequestMapper;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Repository.RequestRepository;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Repository.DepartmentRepository;
import com.example.qlns.Service.RequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepo;
    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final RequestMapper requestMapper;

    public RequestServiceImpl(RequestRepository requestRepo, 
                              EmployeeRepository employeeRepo,
                              UserRepository userRepo,
                              DepartmentRepository deptRepo,
                              RequestMapper requestMapper) {
        this.requestRepo = requestRepo;
        this.employeeRepo = employeeRepo;
        this.userRepo = userRepo;
        this.deptRepo = deptRepo;
        this.requestMapper = requestMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDTO> getMyRequests(Long employeeId) {
        return requestRepo.findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream().map(requestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDTO createRequest(Long employeeId, CreateRequestRequest req) {
        if (req.getTitle() == null || req.getTitle().isBlank())
            throw new BadRequestException("Tiêu đề đơn không được để trống");

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        Request r = new Request();
        r.setEmployee(emp);
        r.setTitle(req.getTitle().trim());
        r.setDescription(req.getDescription());
        r.setFileUrl(req.getFileUrl());
        r.setFileName(req.getFileName());

        return requestMapper.toDTO(requestRepo.save(r));
    }

    @Override
    @Transactional
    public RequestDTO updateRequest(Long requestId, Long employeeId, CreateRequestRequest req) {
        Request r = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn"));

        if (!r.getEmployee().getId().equals(employeeId))
            throw new ForbiddenException("Bạn không có quyền sửa đơn này");

        if (r.getStatus() != RequestStatus.PENDING)
            throw new BadRequestException("Chỉ được sửa đơn khi chưa được duyệt");

        if (req.getTitle() != null && !req.getTitle().isBlank())
            r.setTitle(req.getTitle().trim());
        if (req.getDescription() != null)
            r.setDescription(req.getDescription());
        if (req.getFileUrl() != null)
            r.setFileUrl(req.getFileUrl());
        if (req.getFileName() != null)
            r.setFileName(req.getFileName());

        return requestMapper.toDTO(requestRepo.save(r));
    }

    @Override
    @Transactional
    public void cancelRequest(Long requestId, Long employeeId) {
        Request r = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn"));

        if (!r.getEmployee().getId().equals(employeeId))
            throw new ForbiddenException("Bạn không có quyền huỷ đơn này");

        if (r.getStatus() != RequestStatus.PENDING)
            throw new BadRequestException("Chỉ được huỷ đơn khi chưa được duyệt");

        requestRepo.delete(r);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDTO> getByDepartment(Long deptId) {
        return requestRepo.findByDepartmentId(deptId)
                .stream().map(requestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDTO> getByDepartmentAndStatus(Long deptId, String status) {
        RequestStatus reqStatus = RequestStatus.valueOf(status);
        return requestRepo.findByDepartmentIdAndStatus(deptId, reqStatus)
                .stream().map(requestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDTO> getPendingByDepartment(Long deptId) {
        return requestRepo.findByDepartmentIdAndStatus(deptId, RequestStatus.PENDING)
                .stream().map(requestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDTO getRequestById(Long id) {
        Request r = requestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn id=" + id));
        return requestMapper.toDTO(r);
    }

    @Override
    @Transactional
    public RequestDTO reviewRequest(Long requestId, Long reviewerId, ReviewRequestRequest req) {
        Request r = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn"));

        if (r.getStatus() != RequestStatus.PENDING)
            throw new BadRequestException("Đơn này đã được xử lý rồi");

        Employee reviewer = employeeRepo.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người duyệt"));

        if (req.isApproved()) {
            r.setStatus(RequestStatus.APPROVED);
        } else {
            if (req.getRejectionReason() == null || req.getRejectionReason().isBlank())
                throw new BadRequestException("Phải nhập lý do từ chối");
            r.setStatus(RequestStatus.REJECTED);
            r.setRejectionReason(req.getRejectionReason());
        }

        r.setReviewedBy(reviewer);
        return requestMapper.toDTO(requestRepo.save(r));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDTO> getAllRequests() {
        return requestRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(requestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDTO> getAllRequestsByStatus(String status) {
        RequestStatus reqStatus = RequestStatus.valueOf(status);
        return requestRepo.findByStatusOrderByCreatedAtDesc(reqStatus)
                .stream().map(requestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RequestStatsDTO getRequestStats(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (user.getRole() == Role.ADMIN) {
            return new RequestStatsDTO(
                requestRepo.countByStatus(RequestStatus.PENDING),
                requestRepo.countByKeyword("Nghỉ"),
                requestRepo.countByKeyword("Tăng ca"),
                requestRepo.countByKeyword("Công tác")
            );
        } else if (user.getRole() == Role.MANAGER) {
            Long deptId = deptRepo.findByManagerId(user.getEmployeeId())
                    .map(Department::getId)
                    .orElse(null);
            if (deptId != null) {
                return new RequestStatsDTO(
                    requestRepo.countByDepartmentIdAndStatus(deptId, RequestStatus.PENDING),
                    requestRepo.countByDepartmentAndKeyword(deptId, "Nghỉ"),
                    requestRepo.countByDepartmentAndKeyword(deptId, "Tăng ca"),
                    requestRepo.countByDepartmentAndKeyword(deptId, "Công tác")
                );
            }
        }
        
        Long empId = user.getEmployeeId();
        if (empId != null) {
            return new RequestStatsDTO(
                requestRepo.countByEmployeeIdAndStatus(empId, RequestStatus.PENDING),
                requestRepo.countByEmployeeAndKeyword(empId, "Nghỉ"),
                requestRepo.countByEmployeeAndKeyword(empId, "Tăng ca"),
                requestRepo.countByEmployeeAndKeyword(empId, "Công tác")
            );
        }
        
        return new RequestStatsDTO(0, 0, 0, 0);
    }
}

package com.example.qlns.Service;

import com.example.qlns.DTO.Request.CreateRequestRequest;
import com.example.qlns.DTO.Request.ReviewRequestRequest;
import com.example.qlns.DTO.Response.RequestDTO;

import java.util.List;

public interface RequestService {
    List<RequestDTO> getMyRequests(Long employeeId);
    RequestDTO createRequest(Long employeeId, CreateRequestRequest req);
    RequestDTO updateRequest(Long requestId, Long employeeId, CreateRequestRequest req);
    void cancelRequest(Long requestId, Long employeeId);
    List<RequestDTO> getByDepartment(Long deptId);
    List<RequestDTO> getByDepartmentAndStatus(Long deptId, String status);
    List<RequestDTO> getPendingByDepartment(Long deptId);
    RequestDTO getRequestById(Long id);
    RequestDTO reviewRequest(Long requestId, Long reviewerId, ReviewRequestRequest req);
    List<RequestDTO> getAllRequests();
    List<RequestDTO> getAllRequestsByStatus(String status);
    com.example.qlns.DTO.Response.RequestStatsDTO getRequestStats(Long userId);
}
package com.example.qlns.Mapper;

import com.example.qlns.DTO.Response.RequestDTO;
import com.example.qlns.Entity.Request;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public RequestDTO toDTO(Request r) {
        if (r == null) return null;
        
        RequestDTO dto = new RequestDTO();
        dto.setId(r.getId());
        dto.setEmployeeId(r.getEmployee() != null ? r.getEmployee().getId() : null);
        dto.setEmployeeName(r.getEmployee() != null ? r.getEmployee().getFullName() : null);
        dto.setDepartmentName(r.getEmployee() != null && r.getEmployee().getDepartment() != null
                ? r.getEmployee().getDepartment().getName() : null);
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setFileUrl(r.getFileUrl());
        dto.setFileName(r.getFileName());
        dto.setStatus(r.getStatus());
        
        if (r.getReviewedBy() != null) {
            dto.setReviewedById(r.getReviewedBy().getId());
            dto.setReviewedByName(r.getReviewedBy().getFullName());
        }
        
        dto.setRejectionReason(r.getRejectionReason());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        
        return dto;
    }
}

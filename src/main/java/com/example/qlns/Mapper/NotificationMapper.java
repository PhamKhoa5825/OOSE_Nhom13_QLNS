package com.example.qlns.Mapper;

import com.example.qlns.DTO.Response.NotificationDTO;
import com.example.qlns.Entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification n, boolean isRead) {
        if (n == null) return null;
        
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setContent(n.getContent());
        dto.setTargetType(n.getTargetType() != null ? n.getTargetType().name() : null);
        dto.setCreatedAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
        dto.setRead(isRead);
        
        if (n.getDepartment() != null) {
            dto.setDepartmentId(n.getDepartment().getId());
            dto.setDepartmentName(n.getDepartment().getName());
        }
        
        if (n.getCreatedBy() != null) {
            dto.setCreatedByName(n.getCreatedBy().getFullName());
        }
        
        return dto;
    }
}

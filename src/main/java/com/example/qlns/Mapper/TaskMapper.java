package com.example.qlns.Mapper;

import com.example.qlns.DTO.Response.TaskDTO;
import com.example.qlns.Entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDTO toDTO(Task t) {
        if (t == null) return null;
        
        TaskDTO dto = new TaskDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setPriority(t.getPriority() != null ? t.getPriority().name() : null);
        dto.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
        dto.setDeadline(t.getDeadline() != null ? t.getDeadline().toString() : null);
        dto.setCompletedAt(t.getCompletedAt() != null ? t.getCompletedAt().toString() : null);
        dto.setAttachmentUrl(t.getAttachmentUrl());
        dto.setCreatedAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null);
        
        if (t.getAssignedBy() != null) {
            dto.setAssignedById(t.getAssignedBy().getId());
            dto.setAssignedByName(t.getAssignedBy().getFullName());
        }
        
        if (t.getAssignedTo() != null) {
            dto.setAssignedToId(t.getAssignedTo().getId());
            dto.setAssignedToName(t.getAssignedTo().getFullName());
        }
        
        return dto;
    }
}

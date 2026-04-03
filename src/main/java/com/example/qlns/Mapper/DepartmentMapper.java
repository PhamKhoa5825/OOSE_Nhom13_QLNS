package com.example.qlns.Mapper;

import com.example.qlns.DTO.Response.DepartmentDTO;
import com.example.qlns.Entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentDTO toDTO(Department dept, int employeeCount) {
        if (dept == null) return null;
        
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(dept.getId());
        dto.setName(dept.getName());
        dto.setDescription(dept.getDescription());
        dto.setEmployeeCount(employeeCount);
        dto.setCreatedAt(dept.getCreatedAt() != null ? dept.getCreatedAt().toString() : null);
        
        if (dept.getManager() != null) {
            dto.setManagerId(dept.getManager().getId());
            dto.setManagerName(dept.getManager().getFullName());
        }
        
        return dto;
    }
}

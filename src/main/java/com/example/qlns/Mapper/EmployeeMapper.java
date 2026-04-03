package com.example.qlns.Mapper;

import com.example.qlns.DTO.Response.EmployeeDTO;
import com.example.qlns.DTO.Response.EmployeeDetailDTO;
import com.example.qlns.DTO.Response.EmployeeSummaryDTO;
import com.example.qlns.Entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDTO toDTO(Employee emp) {
        return toDTO(emp, null, null);
    }

    public EmployeeDTO toDTO(Employee emp, String role, String userStatus) {
        if (emp == null) return null;
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(emp.getId());
        dto.setFullName(emp.getFullName());
        dto.setEmail(emp.getEmail());
        dto.setPhone(emp.getPhone());
        dto.setAddress(emp.getAddress());
        dto.setDateOfBirth(emp.getDateOfBirth());
        dto.setGender(emp.getGender() != null ? emp.getGender().name() : null);
        dto.setAvatarUrl(emp.getAvatarUrl());
        dto.setPosition(emp.getPosition());
        dto.setJoinDate(emp.getJoinDate());
        dto.setStatus(emp.getStatus().name());
        dto.setUserStatus(userStatus);
        dto.setRole(role);
        if (emp.getDepartment() != null) {
            dto.setDepartmentId(emp.getDepartment().getId());
            dto.setDepartmentName(emp.getDepartment().getName());
        }
        return dto;
    }

    public EmployeeSummaryDTO toSummaryDTO(Employee emp) {
        if (emp == null) return null;
        return new EmployeeSummaryDTO(emp.getFullName(), emp.getAvatarUrl());
    }

    public EmployeeDetailDTO toDetailDTO(Employee emp, String role) {
        if (emp == null) return null;
        String deptName = (emp.getDepartment() != null) ? emp.getDepartment().getName() : "Chưa có phòng ban";
        return new EmployeeDetailDTO(
                emp.getId(),
                emp.getFullName(),
                emp.getAvatarUrl(),
                emp.getPosition(),
                deptName,
                emp.getEmail(),
                emp.getPhone(),
                emp.getAddress(),
                role,
                emp.getDateOfBirth(),
                emp.getGender() != null ? emp.getGender().name() : null,
                emp.getJoinDate()
        );
    }
}

package com.example.qlns.Mapper;

import com.example.qlns.DTO.Response.AttendanceDTO;
import com.example.qlns.Entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceDTO toDTO(Attendance a) {
        if (a == null) return null;
        
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(a.getId());
        dto.setDate(a.getDate() != null ? a.getDate().toString() : null);
        dto.setWorkHours(a.getWorkHours());
        dto.setLocationLat(a.getLocationLat());
        dto.setLocationLng(a.getLocationLng());
        dto.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
        dto.setCheckIn(a.getCheckIn() != null ? a.getCheckIn().toString() : null);
        dto.setCheckOut(a.getCheckOut() != null ? a.getCheckOut().toString() : null);
        dto.setLateMinutes(a.getLateMinutes());
        
        if (a.getEmployee() != null) {
            dto.setEmployeeId(a.getEmployee().getId());
            dto.setEmployeeName(a.getEmployee().getFullName());
        }
        
        return dto;
    }
}

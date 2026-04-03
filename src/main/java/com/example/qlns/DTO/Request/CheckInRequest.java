package com.example.qlns.DTO.Request;

// TV3
public class CheckInRequest {
    private Long employeeId;
    private Double latitude;
    private Double longitude;

    public Long getEmployeeId() {
        return employeeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}

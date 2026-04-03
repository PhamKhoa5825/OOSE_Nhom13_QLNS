package com.example.qlns.DTO.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String date;
    private String checkIn;
    private String checkOut;
    private Float workHours;
    private Double locationLat;
    private Double locationLng;
    private String status;
    private Integer lateMinutes;

    public AttendanceDTO() {}

    // Getters
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDate() { return date; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
    public Float getWorkHours() { return workHours; }
    public Double getLocationLat() { return locationLat; }
    public Double getLocationLng() { return locationLng; }
    public String getStatus() { return status; }
    public Integer getLateMinutes() { return lateMinutes; }

    // Helpers for Thymeleaf template rendering
    @JsonIgnore
    public LocalDate getParsedDate() {
        return date != null ? LocalDate.parse(date) : null;
    }

    @JsonIgnore
    public LocalDateTime getParsedCheckIn() {
        return checkIn != null ? LocalDateTime.parse(checkIn) : null;
    }

    @JsonIgnore
    public LocalDateTime getParsedCheckOut() {
        return checkOut != null ? LocalDateTime.parse(checkOut) : null;
    }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setDate(String date) { this.date = date; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
    public void setWorkHours(Float workHours) { this.workHours = workHours; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
    public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }
    public void setStatus(String status) { this.status = status; }
    public void setLateMinutes(Integer lateMinutes) { this.lateMinutes = lateMinutes; }
}

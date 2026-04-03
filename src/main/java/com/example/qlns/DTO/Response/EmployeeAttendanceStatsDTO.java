package com.example.qlns.DTO.Response;

public class EmployeeAttendanceStatsDTO {
    private Long employeeId;
    private String fullName;
    private long onTimeCount;
    private long lateCount;
    private long absentCount;

    public EmployeeAttendanceStatsDTO(Long employeeId, String fullName, long onTimeCount, long lateCount, long absentCount) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.onTimeCount = onTimeCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
    }

    // Getters
    public Long getEmployeeId() { return employeeId; }
    public String getFullName() { return fullName; }
    public long getOnTimeCount() { return onTimeCount; }
    public long getLateCount() { return lateCount; }
    public long getAbsentCount() { return absentCount; }
}

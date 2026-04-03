package com.example.qlns.DTO.Request;

public class CreateLeaveRequest {
    private Long employeeId;
    private String leaveType;       // ANNUAL/SICK/MATERNITY/LATE/EARLY/OVERTIME
    private String startDate;
    private String endDate;
    private String reason;

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }
}

package com.example.qlns.DTO.Response;

public class RequestStatsDTO {
    private long pendingCount;
    private long leaveCount;
    private long overtimeCount;
    private long businessCount;

    public RequestStatsDTO() {}

    public RequestStatsDTO(long pendingCount, long leaveCount, long overtimeCount, long businessCount) {
        this.pendingCount = pendingCount;
        this.leaveCount = leaveCount;
        this.overtimeCount = overtimeCount;
        this.businessCount = businessCount;
    }

    public long getPendingCount() { return pendingCount; }
    public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }

    public long getLeaveCount() { return leaveCount; }
    public void setLeaveCount(long leaveCount) { this.leaveCount = leaveCount; }

    public long getOvertimeCount() { return overtimeCount; }
    public void setOvertimeCount(long overtimeCount) { this.overtimeCount = overtimeCount; }

    public long getBusinessCount() { return businessCount; }
    public void setBusinessCount(long businessCount) { this.businessCount = businessCount; }
}

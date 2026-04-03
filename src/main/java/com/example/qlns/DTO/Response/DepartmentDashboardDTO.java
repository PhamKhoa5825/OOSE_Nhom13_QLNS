package com.example.qlns.DTO.Response;

/**
 * DTO for department dashboard statistics.
 * Provides attendance, task, and employee summary data.
 */
public class DepartmentDashboardDTO {

    // ── Tổng quan ──────────────────────────────
    private long totalEmployees;

    // ── Thống kê chấm công (theo tháng) ────────
    private long onTimeCount;       // Số lượt đúng giờ
    private long lateCount;         // Số lượt đi muộn
    private long absentCount;       // Số lượt vắng (NV chưa chấm công ngày làm việc)
    private double onTimeRate;      // % đúng giờ
    private double lateRate;        // % đi muộn
    private double absentRate;      // % vắng

    // ── Thống kê nhiệm vụ ─────────────────────
    private long taskInProgress;    // ACCEPTED
    private long taskCompleted;     // DONE
    private long taskOverdue;       // OVERDUE
    private long taskPending;       // PENDING
    private long taskTotal;

    public DepartmentDashboardDTO() {}

    // ── Getters & Setters ─────────────────────
    public long getTotalEmployees()      { return totalEmployees; }
    public void setTotalEmployees(long v) { this.totalEmployees = v; }

    public long getOnTimeCount()         { return onTimeCount; }
    public void setOnTimeCount(long v)   { this.onTimeCount = v; }

    public long getLateCount()           { return lateCount; }
    public void setLateCount(long v)     { this.lateCount = v; }

    public long getAbsentCount()         { return absentCount; }
    public void setAbsentCount(long v)   { this.absentCount = v; }

    public double getOnTimeRate()        { return onTimeRate; }
    public void setOnTimeRate(double v)  { this.onTimeRate = v; }

    public double getLateRate()          { return lateRate; }
    public void setLateRate(double v)    { this.lateRate = v; }

    public double getAbsentRate()        { return absentRate; }
    public void setAbsentRate(double v)  { this.absentRate = v; }

    public long getTaskInProgress()      { return taskInProgress; }
    public void setTaskInProgress(long v) { this.taskInProgress = v; }

    public long getTaskCompleted()       { return taskCompleted; }
    public void setTaskCompleted(long v) { this.taskCompleted = v; }

    public long getTaskOverdue()         { return taskOverdue; }
    public void setTaskOverdue(long v)   { this.taskOverdue = v; }

    public long getTaskPending()         { return taskPending; }
    public void setTaskPending(long v)   { this.taskPending = v; }

    public long getTaskTotal()           { return taskTotal; }
    public void setTaskTotal(long v)     { this.taskTotal = v; }
}

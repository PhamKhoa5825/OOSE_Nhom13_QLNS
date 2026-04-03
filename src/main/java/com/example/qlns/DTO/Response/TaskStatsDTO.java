package com.example.qlns.DTO.Response;

public class TaskStatsDTO {
    private long total;
    private long completed;
    private long inProgress;
    private long pending;
    private long overdue;

    public TaskStatsDTO() {}

    public TaskStatsDTO(long total, long completed, long inProgress, long pending, long overdue) {
        this.total = total;
        this.completed = completed;
        this.inProgress = inProgress;
        this.pending = pending;
        this.overdue = overdue;
    }

    // Getters and Setters
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getCompleted() { return completed; }
    public void setCompleted(long completed) { this.completed = completed; }

    public long getInProgress() { return inProgress; }
    public void setInProgress(long inProgress) { this.inProgress = inProgress; }

    public long getPending() { return pending; }
    public void setPending(long pending) { this.pending = pending; }

    public long getOverdue() { return overdue; }
    public void setOverdue(long overdue) { this.overdue = overdue; }
}

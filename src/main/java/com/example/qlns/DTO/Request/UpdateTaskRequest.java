package com.example.qlns.DTO.Request;

/**
 * DTO for updating an existing task.
 * Manager can modify: title, description, priority, deadline, assignedToId.
 */
public class UpdateTaskRequest {
    private String title;
    private String description;
    private String priority;        // "HIGH", "MEDIUM", "LOW", "URGENT"
    private String deadline;        // ISO format: "2026-04-01"
    private Long assignedToId;      // Nhân viên được giao (có thể đổi người)

    public String getTitle()          { return title; }
    public String getDescription()    { return description; }
    public String getPriority()       { return priority; }
    public String getDeadline()       { return deadline; }
    public Long getAssignedToId()     { return assignedToId; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
}

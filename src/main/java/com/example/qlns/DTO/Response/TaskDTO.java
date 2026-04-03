package com.example.qlns.DTO.Response;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String deadline;
    private String completedAt;
    private String attachmentUrl;
    private Long assignedById;
    private String assignedByName;
    private Long assignedToId;
    private String assignedToName;
    private String createdAt;

    public TaskDTO() {}

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getDeadline() { return deadline; }
    public String getCompletedAt() { return completedAt; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public Long getAssignedById() { return assignedById; }
    public String getAssignedByName() { return assignedByName; }
    public Long getAssignedToId() { return assignedToId; }
    public String getAssignedToName() { return assignedToName; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setStatus(String status) { this.status = status; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public void setAssignedById(Long assignedById) { this.assignedById = assignedById; }
    public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
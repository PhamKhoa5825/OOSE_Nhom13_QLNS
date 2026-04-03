package com.example.qlns.DTO.Response;

public class NotificationDTO {
    private Long id;
    private String title;
    private String content;
    private String targetType;
    private Long departmentId;
    private String departmentName;
    private String createdByName;
    private String createdAt;
    private boolean isRead;

    public NotificationDTO() {}

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTargetType() { return targetType; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public String getCreatedByName() { return createdByName; }
    public String getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setRead(boolean read) { isRead = read; }
}

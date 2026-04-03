package com.example.qlns.DTO.Request;

public class CreateNotificationRequest {
    private String title;
    private String content;
    private String targetType;      // COMPANY / DEPARTMENT
    private Long departmentId;
    private Long createdById;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTargetType() {
        return targetType;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Long getCreatedById() {
        return createdById;
    }
}

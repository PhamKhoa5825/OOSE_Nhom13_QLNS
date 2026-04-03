package com.example.qlns.DTO.Request;

public class CreateTaskRequest {
    private String title;           // Bắt buộc
    private String description;
    private String priority;        // "HIGH", "MEDIUM", "LOW", "URGENT"
    private String deadline;        // ISO format: "2026-04-01"
    private String attachmentUrl;
    private Long assignedToId;      // Bắt buộc - nhân viên được giao
    private Long assignedById;      // Bắt buộc - người giao (Manager/Admin)

    public String getTitle()          { return title; }
    public String getDescription()    { return description; }
    public String getPriority()       { return priority; }
    public String getDeadline()       { return deadline; }
    public String getAttachmentUrl()  { return attachmentUrl; }
    public Long getAssignedToId()     { return assignedToId; }
    public Long getAssignedById()     { return assignedById; }
}
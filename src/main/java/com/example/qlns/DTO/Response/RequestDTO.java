package com.example.qlns.DTO.Response;

import com.example.qlns.Enum.RequestStatus;
import java.time.LocalDateTime;

public class RequestDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String departmentName;
    private String title;
    private String description;
    private String fileUrl;
    private String fileName;
    private RequestStatus status;
    private Long reviewedById;
    private String reviewedByName;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RequestDTO() {}

    // Getters
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDepartmentName() { return departmentName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getFileUrl() { return fileUrl; }
    public String getFileName() { return fileName; }
    public RequestStatus getStatus() { return status; }
    public Long getReviewedById() { return reviewedById; }
    public String getReviewedByName() { return reviewedByName; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public void setReviewedById(Long reviewedById) { this.reviewedById = reviewedById; }
    public void setReviewedByName(String reviewedByName) { this.reviewedByName = reviewedByName; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

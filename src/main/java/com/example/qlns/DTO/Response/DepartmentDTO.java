package com.example.qlns.DTO.Response;

public class DepartmentDTO {
    private Long id;
    private String name;
    private String description;
    private Long managerId;
    private String managerName;
    private int employeeCount;
    private String createdAt;

    public DepartmentDTO() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getManagerId() { return managerId; }
    public String getManagerName() { return managerName; }
    public int getEmployeeCount() { return employeeCount; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public void setEmployeeCount(int employeeCount) { this.employeeCount = employeeCount; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

package com.example.qlns.DTO.Response;

import java.time.LocalDate;

public class EmployeeDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String position;
    private String avatarUrl;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDate joinDate;
    private String status;
    private String role;
    private String userStatus;
    private Long departmentId;
    private String departmentName;

    public EmployeeDTO() {}

    // Getters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getPosition() { return position; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getGender() { return gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public LocalDate getJoinDate() { return joinDate; }
    public String getStatus() { return status; }
    public String getRole() { return role; }
    public String getUserStatus() { return userStatus; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setPosition(String position) { this.position = position; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setGender(String gender) { this.gender = gender; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    public void setStatus(String status) { this.status = status; }
    public void setRole(String role) { this.role = role; }
    public void setUserStatus(String userStatus) { this.userStatus = userStatus; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}

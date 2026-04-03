package com.example.qlns.DTO.Response;

import java.time.LocalDate;

/**
 * Employee Detail DTO
 * Trả về thông tin đầy đủ cho trang cá nhân và chỉnh sửa
 */
public class EmployeeDetailDTO {
    private Long id;
    private String fullName;
    private String avatarUrl;
    private String position;
    private String departmentName;
    private String email;
    private String phone;
    private String address;
    private String role;
    private LocalDate dateOfBirth;
    private String gender;
    private LocalDate joinDate;

    public EmployeeDetailDTO() {}

    public EmployeeDetailDTO(Long id, String fullName, String avatarUrl, String position, 
                             String departmentName, String email, String phone, 
                             String address, String role, LocalDate dateOfBirth,
                             String gender, LocalDate joinDate) {
        this.id = id;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.position = position;
        this.departmentName = departmentName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.joinDate = joinDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
}

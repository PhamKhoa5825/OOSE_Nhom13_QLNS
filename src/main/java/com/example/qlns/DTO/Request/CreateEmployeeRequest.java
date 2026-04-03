package com.example.qlns.DTO.Request;

// TV1
public class CreateEmployeeRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String dateOfBirth;     // "yyyy-MM-dd"
    private String gender;          // MALE / FEMALE / OTHER
    private String position;
    private String joinDate;
    private Long departmentId;
    private String role; // ADMIN, MANAGER, EMPLOYEE

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
    
    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPosition() {
        return position;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public Long getDepartmentId() {
        return departmentId;
    }
}

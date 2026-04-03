package com.example.qlns.DTO.Request;

public class UpdateEmployeeRequest {
    private String fullName;
    private String phone;
    private String address;
    private String position;
    private Long departmentId;
    private String status;          // ACTIVE / RESIGNED
    private String avatarUrl;
    private String gender;
    private String dateOfBirth;

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPosition() {
        return position;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getStatus() {
        return status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}

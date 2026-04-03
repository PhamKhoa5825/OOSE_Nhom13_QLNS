package com.example.qlns.DTO.Response;

/**
 * Employee Summary DTO
 * Trả về thông tin rút gọn cho trang chủ (Chỉ gồm Họ tên và Ảnh)
 */
public class EmployeeSummaryDTO {
    private String fullName;
    private String avatarUrl;

    public EmployeeSummaryDTO() {}

    public EmployeeSummaryDTO(String fullName, String avatarUrl) {
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

package com.example.qlns.DTO.Response;

// TV2
class LoginResponse {
    private String token;
    private String role;
    private Long userId;
    private Long employeeId;
    private String fullName;
    private String avatarUrl;

    public LoginResponse(String token, String role, Long userId,
                         Long employeeId, String fullName, String avatarUrl) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}

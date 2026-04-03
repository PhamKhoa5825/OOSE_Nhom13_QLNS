package com.example.qlns.DTO.Request;

public class ApproveLeaveRequest {
    private String action;          // APPROVE / REJECT
    private String rejectionReason;

    public String getAction() {
        return action;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}

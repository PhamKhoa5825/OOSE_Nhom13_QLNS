package com.example.qlns.DTO.Request;

// ── ReviewRequestRequest (Manager/Admin → Server) ─────────────
public class ReviewRequestRequest {
    private boolean approved;           // true = duyệt, false = từ chối
    private String rejectionReason;     // Bắt buộc nếu approved = false

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}

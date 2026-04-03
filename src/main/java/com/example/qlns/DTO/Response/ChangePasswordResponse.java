package com.example.qlns.DTO.Response;

/**
 * Change Password Response DTO
 * Returned after successful password change
 */
public class ChangePasswordResponse {
    
    private String message;
    private boolean success;
    private long timestamp;

    public ChangePasswordResponse() {}

    public ChangePasswordResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}


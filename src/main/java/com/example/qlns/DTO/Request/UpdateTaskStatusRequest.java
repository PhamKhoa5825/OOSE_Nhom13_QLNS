package com.example.qlns.DTO.Request;

public class UpdateTaskStatusRequest {
    private String status;          // TODO/IN_PROGRESS/DONE
    private String note;

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }
}

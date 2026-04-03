package com.example.qlns.DTO.Request;

// TV4
public class SendMessageRequest {
    private Long roomId;
    private Long senderId;
    private String message;
    private String messageType;     // TEXT/IMAGE/FILE

    public Long getRoomId() {
        return roomId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageType() {
        return messageType;
    }
}

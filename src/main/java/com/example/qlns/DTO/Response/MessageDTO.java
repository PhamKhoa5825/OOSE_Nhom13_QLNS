package com.example.qlns.DTO.Response;

public class MessageDTO {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String message;
    private String messageType;
    private String createdAt;

    public MessageDTO() {}

    // Getters
    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getSenderAvatar() { return senderAvatar; }
    public String getMessage() { return message; }
    public String getMessageType() { return messageType; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }
    public void setMessage(String message) { this.message = message; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

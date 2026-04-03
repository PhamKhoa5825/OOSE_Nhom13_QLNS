package com.example.qlns.DTO.Response;

import java.util.List;

public class ChatRoomDTO {
    private Long id;
    private String name;
    private String type;
    private Long departmentId;
    private String lastMessage;
    private String lastMessageTime;
    private List<String> memberNames;
    private String otherParticipantName;

    public ChatRoomDTO() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public Long getDepartmentId() { return departmentId; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTime() { return lastMessageTime; }
    public List<String> getMemberNames() { return memberNames; }
    public String getOtherParticipantName() { return otherParticipantName; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    public void setMemberNames(List<String> memberNames) { this.memberNames = memberNames; }
    public void setOtherParticipantName(String otherParticipantName) { this.otherParticipantName = otherParticipantName; }
}

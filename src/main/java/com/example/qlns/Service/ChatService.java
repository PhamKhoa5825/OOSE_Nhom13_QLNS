package com.example.qlns.Service;

import com.example.qlns.Entity.ChatRoom;
import com.example.qlns.Entity.Message;
import com.example.qlns.Enum.MessageType;

import java.util.List;

public interface ChatService {
    List<ChatRoom> getRoomsForUser(Long userId);
    ChatRoom getOrCreatePrivateRoom(Long userId1, Long userId2);
    ChatRoom createDepartmentRoom(Long departmentId, String name);
    void addMember(Long roomId, Long userId);
    Message sendMessage(Long roomId, Long senderId, String content, MessageType type);
    List<Message> getMessages(Long roomId);
    List<Message> getNewMessages(Long roomId, Long lastMessageId);
}

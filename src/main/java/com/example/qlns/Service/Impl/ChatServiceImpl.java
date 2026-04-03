package com.example.qlns.Service.Impl;

import com.example.qlns.Entity.ChatRoom;
import com.example.qlns.Entity.ChatRoomMember;
import com.example.qlns.Entity.Department;
import com.example.qlns.Entity.Message;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.ChatRoomType;
import com.example.qlns.Enum.MessageType;
import com.example.qlns.Exception.ForbiddenException;
import com.example.qlns.Exception.ResourceNotFoundException;
import com.example.qlns.Repository.ChatRoomMemberRepository;
import com.example.qlns.Repository.ChatRoomRepository;
import com.example.qlns.Repository.DepartmentRepository;
import com.example.qlns.Repository.MessageRepository;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository roomRepo;
    private final ChatRoomMemberRepository memberRepo;
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;

    public ChatServiceImpl(ChatRoomRepository roomRepo, ChatRoomMemberRepository memberRepo,
                           MessageRepository messageRepo, UserRepository userRepo,
                           DepartmentRepository deptRepo) {
        this.roomRepo = roomRepo;
        this.memberRepo = memberRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.deptRepo = deptRepo;
    }

    @Override
    public List<ChatRoom> getRoomsForUser(Long userId) {
        return memberRepo.findByUserId(userId).stream()
                .map(ChatRoomMember::getRoom)
                .toList();
    }

    @Override
    @Transactional
    public ChatRoom getOrCreatePrivateRoom(Long userId1, Long userId2) {
        return roomRepo.findPrivateRoom(userId1, userId2).orElseGet(() -> {
            ChatRoom room = new ChatRoom();
            room.setType(ChatRoomType.PRIVATE);
            ChatRoom saved = roomRepo.save(room);
            addMember(saved.getId(), userId1);
            addMember(saved.getId(), userId2);
            return saved;
        });
    }

    @Override
    @Transactional
    public ChatRoom createDepartmentRoom(Long departmentId, String name) {
        Department dept = deptRepo.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng ban"));
        ChatRoom room = new ChatRoom();
        room.setName(name);
        room.setType(ChatRoomType.DEPARTMENT);
        room.setDepartment(dept);
        return roomRepo.save(room);
    }

    @Override
    public void addMember(Long roomId, Long userId) {
        if (!memberRepo.existsByRoomIdAndUserId(roomId, userId)) {
            ChatRoomMember member = new ChatRoomMember();
            roomRepo.findById(roomId).ifPresent(member::setRoom);
            userRepo.findById(userId).ifPresent(member::setUser);
            memberRepo.save(member);
        }
    }

    @Override
    @Transactional
    public Message sendMessage(Long roomId, Long senderId, String content, MessageType type) {
        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng chat"));
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        if (!memberRepo.existsByRoomIdAndUserId(roomId, senderId))
            throw new ForbiddenException("Bạn không trong phòng chat này");

        Message msg = new Message();
        msg.setRoom(room);
        msg.setSender(sender);
        msg.setMessage(content);
        msg.setMessageType(type);
        return messageRepo.save(msg);
    }

    @Override
    public List<Message> getMessages(Long roomId) {
        return messageRepo.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    @Override
    public List<Message> getNewMessages(Long roomId, Long lastMessageId) {
        return messageRepo.findNewMessages(roomId, lastMessageId);
    }
}

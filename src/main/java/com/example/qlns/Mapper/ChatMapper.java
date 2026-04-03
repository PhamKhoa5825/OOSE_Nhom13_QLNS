package com.example.qlns.Mapper;

import com.example.qlns.DTO.Response.ChatRoomDTO;
import com.example.qlns.DTO.Response.MessageDTO;
import com.example.qlns.Entity.ChatRoom;
import com.example.qlns.Entity.ChatRoomMember;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.Message;
import com.example.qlns.Repository.ChatRoomMemberRepository;
import com.example.qlns.Repository.EmployeeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatMapper {

    private final EmployeeRepository empRepo;
    private final ChatRoomMemberRepository memberRepo;

    public ChatMapper(EmployeeRepository empRepo, ChatRoomMemberRepository memberRepo) {
        this.empRepo = empRepo;
        this.memberRepo = memberRepo;
    }

    public ChatRoomDTO toDTO(ChatRoom room, Long currentUserId) {
        if (room == null) return null;

        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setType(room.getType() != null ? room.getType().name() : null);
        if (room.getDepartment() != null) {
            dto.setDepartmentId(room.getDepartment().getId());
        }

        List<ChatRoomMember> members = memberRepo.findByRoomId(room.getId());
        List<String> names = members.stream()
                .map(m -> {
                    String name = m.getUser().getUsername();
                    if (m.getUser().getEmployeeId() != null) {
                        name = empRepo.findById(m.getUser().getEmployeeId())
                                .map(Employee::getFullName)
                                .orElse(m.getUser().getUsername());
                    }

                    if ("PRIVATE".equals(room.getType().name()) && !m.getUser().getId().equals(currentUserId)) {
                        dto.setOtherParticipantName(name);
                    }

                    return name;
                })
                .collect(Collectors.toList());

        dto.setMemberNames(names);
        return dto;
    }

    public MessageDTO toDTO(Message msg) {
        if (msg == null) return null;

        MessageDTO dto = new MessageDTO();
        dto.setId(msg.getId());
        dto.setMessage(msg.getMessage());
        dto.setMessageType(msg.getMessageType() != null ? msg.getMessageType().name() : null);
        dto.setCreatedAt(msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : null);

        if (msg.getRoom() != null) {
            dto.setRoomId(msg.getRoom().getId());
        }

        if (msg.getSender() != null) {
            dto.setSenderId(msg.getSender().getId());
            if (msg.getSender().getEmployeeId() != null) {
                empRepo.findById(msg.getSender().getEmployeeId())
                        .ifPresent(emp -> dto.setSenderName(emp.getFullName()));
            } else {
                dto.setSenderName(msg.getSender().getUsername());
            }
        }

        return dto;
    }
}

package com.example.qlns.Controller;

import com.example.qlns.DTO.Request.SendMessageRequest;
import com.example.qlns.DTO.Response.ChatRoomDTO;
import com.example.qlns.DTO.Response.MessageDTO;
import com.example.qlns.Entity.ChatRoom;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.Message;
import com.example.qlns.Entity.User;
import com.example.qlns.Enum.EmployeeStatus;
import com.example.qlns.Enum.MessageType;
import com.example.qlns.Mapper.ChatMapper;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
class ChatController {
    private final ChatService chatService;
    private final SecurityService securityService;
    private final EmployeeRepository empRepo;
    private final UserRepository userRepo;
    private final ChatMapper chatMapper;

    ChatController(ChatService chatService, SecurityService securityService,
                   EmployeeRepository empRepo, UserRepository userRepo,
                   ChatMapper chatMapper) {
        this.chatService = chatService;
        this.securityService = securityService;
        this.empRepo = empRepo;
        this.userRepo = userRepo;
        this.chatMapper = chatMapper;
    }

    @GetMapping("/rooms/user/{userId}")
    public ResponseEntity<List<ChatRoomDTO>> getRooms(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(chatService.getRoomsForUser(userId).stream()
                .map(room -> chatMapper.toDTO(room, userId)).collect(Collectors.toList()));
    }

    @PostMapping("/rooms/private")
    public ResponseEntity<ChatRoomDTO> getOrCreatePrivate(@RequestParam("userId1") Long userId1,
                                                          @RequestParam("userId2") Long userId2) {
        return ResponseEntity.ok(chatMapper.toDTO(chatService.getOrCreatePrivateRoom(userId1, userId2), userId1));
    }

    @PostMapping("/rooms/group")
    public ResponseEntity<ChatRoomDTO> createGroupRoom(
            @RequestParam("name") String name,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam("creatorUserId") Long creatorUserId) {

        securityService.validateManagerDepartment(departmentId);

        ChatRoom room = chatService.createDepartmentRoom(departmentId, name);

        List<Employee> employees = empRepo.findByDepartmentIdAndStatus(departmentId, EmployeeStatus.ACTIVE);
        for (Employee emp : employees) {
            Optional<User> user = userRepo.findByEmail(emp.getEmail());
            user.ifPresent(u -> chatService.addMember(room.getId(), u.getId()));
        }

        chatService.addMember(room.getId(), creatorUserId);

        return ResponseEntity.ok(chatMapper.toDTO(room, creatorUserId));
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody SendMessageRequest req) {
        MessageType type = req.getMessageType() != null ?
                MessageType.valueOf(req.getMessageType()) : MessageType.TEXT;
        Message msg = chatService.sendMessage(req.getRoomId(), req.getSenderId(), req.getMessage(), type);
        return ResponseEntity.ok(chatMapper.toDTO(msg));
    }

    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok(chatService.getMessages(roomId).stream()
                .map(chatMapper::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/messages/{roomId}/new")
    public ResponseEntity<List<MessageDTO>> getNewMessages(@PathVariable("roomId") Long roomId,
                                                           @RequestParam("lastMessageId") Long lastMessageId) {
        return ResponseEntity.ok(chatService.getNewMessages(roomId, lastMessageId).stream()
                .map(chatMapper::toDTO).collect(Collectors.toList()));
    }
}

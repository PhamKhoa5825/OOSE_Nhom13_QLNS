package com.example.qlns.Controller;

import com.example.qlns.DTO.Request.CreateNotificationRequest;
import com.example.qlns.DTO.Response.NotificationDTO;
import com.example.qlns.Entity.Notification;
import com.example.qlns.Enum.NotificationTarget;
import com.example.qlns.Mapper.NotificationMapper;
import com.example.qlns.Repository.DepartmentRepository;
import com.example.qlns.Security.SecurityService;
import com.example.qlns.Service.EmployeeCrudService;
import com.example.qlns.Service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
class NotificationController {
    private final NotificationService notiService;
    private final SecurityService securityService;
    private final EmployeeCrudService empService;
    private final DepartmentRepository departmentRepo;
    private final NotificationMapper notificationMapper;

    NotificationController(NotificationService notiService, 
                            SecurityService securityService,
                            EmployeeCrudService empService,
                            DepartmentRepository departmentRepo,
                            NotificationMapper notificationMapper) {
        this.notiService = notiService;
        this.securityService = securityService;
        this.empService = empService;
        this.departmentRepo = departmentRepo;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAll() {
        return ResponseEntity.ok(notiService.getAll());
    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<NotificationDTO>> getForEmployee(@PathVariable("deptId") Long deptId,
                                                                @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(notiService.getForEmployee(deptId, userId));
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> create(@RequestBody CreateNotificationRequest req) {
        if (req.getTargetType() != null
                && NotificationTarget.valueOf(req.getTargetType()) == NotificationTarget.DEPARTMENT
                && req.getDepartmentId() != null) {
            securityService.validateManagerDepartment(req.getDepartmentId());
        }

        Notification noti = new Notification();
        noti.setTitle(req.getTitle());
        noti.setContent(req.getContent());
        noti.setTargetType(NotificationTarget.valueOf(req.getTargetType()));
        
        if (req.getDepartmentId() != null) {
            noti.setDepartment(departmentRepo.findById(req.getDepartmentId()).orElse(null));
        }
        
        if (req.getCreatedById() != null) {
            noti.setCreatedBy(empService.getById(req.getCreatedById()));
        }
        
        return ResponseEntity.ok(notificationMapper.toDTO(notiService.create(noti), false));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        notiService.markAsRead(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(notiService.countUnread(userId));
    }
}

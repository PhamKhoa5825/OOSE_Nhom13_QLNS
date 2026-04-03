package com.example.qlns.Service.Impl;

import com.example.qlns.DTO.Response.NotificationDTO;
import com.example.qlns.Entity.Notification;
import com.example.qlns.Entity.User;
import com.example.qlns.Entity.UserNotification;
import com.example.qlns.Enum.EmployeeStatus;
import com.example.qlns.Enum.NotificationTarget;
import com.example.qlns.Mapper.NotificationMapper;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Repository.NotificationRepository;
import com.example.qlns.Repository.UserNotificationRepository;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notiRepo;
    private final UserNotificationRepository userNotiRepo;
    private final UserRepository userRepo;
    private final EmployeeRepository empRepo;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notiRepo, 
                                   UserNotificationRepository userNotiRepo,
                                   UserRepository userRepo, 
                                   EmployeeRepository empRepo,
                                   NotificationMapper notificationMapper) {
        this.notiRepo = notiRepo;
        this.userNotiRepo = userNotiRepo;
        this.userRepo = userRepo;
        this.empRepo = empRepo;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAll() {
        return notiRepo.findAll().stream()
                .map(n -> notificationMapper.toDTO(n, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Notification create(Notification noti) {
        Notification saved = notiRepo.save(noti);
        List<User> targets;
        if (noti.getTargetType() == NotificationTarget.COMPANY) {
            targets = userRepo.findAll();
        } else {
            targets = empRepo.findByDepartmentIdAndStatus(
                            noti.getDepartment().getId(), EmployeeStatus.ACTIVE)
                    .stream()
                    .map(emp -> userRepo.findByEmail(emp.getEmail()).orElse(null))
                    .filter(u -> u != null)
                    .collect(Collectors.toList());
        }
        for (User u : targets) {
            UserNotification un = new UserNotification();
            un.setUser(u);
            un.setNotification(saved);
            userNotiRepo.save(un);
        }
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getForEmployee(Long deptId, Long userId) {
        return notiRepo.findForEmployee(deptId).stream()
                .map(n -> {
                    boolean isRead = userNotiRepo.findByUserIdAndNotificationId(userId, n.getId())
                            .stream()
                            .anyMatch(UserNotification::isRead);
                    return notificationMapper.toDTO(n, isRead);
                })
                .collect(Collectors.toList());
    }

    @Override
    public long countUnread(Long userId) {
        return userNotiRepo.countByUserIdAndIsRead(userId, false);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        userNotiRepo.findByUserIdAndNotificationId(userId, notificationId)
                .ifPresent(un -> {
                    un.setRead(true);
                    userNotiRepo.save(un);
                });
    }
}

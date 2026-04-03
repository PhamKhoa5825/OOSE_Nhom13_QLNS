package com.example.qlns.Service;

import com.example.qlns.DTO.Response.NotificationDTO;
import com.example.qlns.Entity.Notification;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getAll();
    Notification create(Notification noti);
    List<NotificationDTO> getForEmployee(Long deptId, Long userId);
    long countUnread(Long userId);
    void markAsRead(Long userId, Long notificationId);
}

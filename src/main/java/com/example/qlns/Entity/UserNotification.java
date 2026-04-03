package com.example.qlns.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Theo dõi trạng thái đọc của từng ngườ
@Entity
@Table(name = "user_notifications")
public class UserNotification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public UserNotification() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Notification getNotification() { return notification; }
    public void setNotification(Notification notification) { this.notification = notification; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) {
        isRead = read;
        if (read) readAt = LocalDateTime.now();
    }
    public LocalDateTime getReadAt() { return readAt; }
}


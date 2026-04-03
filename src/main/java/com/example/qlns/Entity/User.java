package com.example.qlns.Entity;

import com.example.qlns.Enum.Role;
import com.example.qlns.Enum.UserStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    // Đặt tên field là passwordHash (camelCase) cho nhất quán
    @Column(name = "password_hash", nullable = false)
    private String passwordHash = "";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.EMPLOYEE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "employee_id")
    private Long employeeId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public User() {}

    // Constructor TV1 dùng khi tạo user mới (password chưa BCrypt, TV2 sẽ encode)
    public User(String username, String email, String password, Role role) {
        this.username     = username;
        this.email        = email;
        this.passwordHash = password;
        this.role         = role;
    }

    public Long getId()                     { return id; }

    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }

    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }

    // TV2 dùng getPasswordHash() / setPasswordHash()
    public String getPasswordHash()         { return passwordHash; }
    public void setPasswordHash(String p)   { this.passwordHash = p; }

    // Alias getPassword() để Spring Security UserDetails không bị lỗi
    // (UserDetailsImpl gọi user.getPassword() → trỏ về passwordHash)
    public String getPassword()             { return passwordHash; }
    public void setPassword(String p) { this.passwordHash = p; }

    public Role getRole()                   { return role; }
    public void setRole(Role r)             { this.role = r; }

    public UserStatus getStatus()           { return status; }
    public void setStatus(UserStatus s)     { this.status = s; }

    public Long getEmployeeId()             { return employeeId; }
    public void setEmployeeId(Long id)      { this.employeeId = id; }

    public LocalDateTime getCreatedAt()     { return createdAt; }
}
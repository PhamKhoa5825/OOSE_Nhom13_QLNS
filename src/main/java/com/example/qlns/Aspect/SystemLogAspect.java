package com.example.qlns.Aspect;

import com.example.qlns.Service.SystemLogService;
import com.example.qlns.Entity.User;
import com.example.qlns.Repository.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SystemLogAspect {

    private final SystemLogService logService;
    private final UserRepository userRepo;

    public SystemLogAspect(SystemLogService logService, UserRepository userRepo) {
        this.logService = logService;
        this.userRepo = userRepo;
    }

    @AfterReturning("within(com.example.qlns.Controller..*) && " +
            "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            " @annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            " @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void logAfterWrite(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String action = detectAction(methodName);
        String description = className + "." + methodName + "()";
        User currentUser = getCurrentUser();
        logService.log(currentUser, action, description);
    }

    private String detectAction(String methodName) {
        String lower = methodName.toLowerCase();
        if (lower.contains("login"))  return "LOGIN";
        if (lower.contains("logout")) return "LOGOUT";
        if (lower.contains("create") || lower.contains("add") || lower.contains("save")) return "CREATE";
        if (lower.contains("update") || lower.contains("edit")) return "UPDATE";
        if (lower.contains("delete") || lower.contains("remove")) return "DELETE";
        return "OTHER";
    }

    private User getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                return userRepo.findByUsername(auth.getName()).orElse(null);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
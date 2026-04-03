package com.example.qlns.Controller;

import com.example.qlns.DTO.Response.EmployeeDTO;
import com.example.qlns.Entity.Employee;
import com.example.qlns.Entity.User;
import com.example.qlns.Mapper.EmployeeMapper;
import com.example.qlns.Repository.EmployeeRepository;
import com.example.qlns.Repository.UserRepository;
import com.example.qlns.Security.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final SecurityService securityService;
    private final UserRepository userRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeMapper employeeMapper;

    public GlobalControllerAdvice(SecurityService securityService, UserRepository userRepo, 
                                 EmployeeRepository empRepo, EmployeeMapper employeeMapper) {
        this.securityService = securityService;
        this.userRepo = userRepo;
        this.empRepo = empRepo;
        this.employeeMapper = employeeMapper;
    }

    @ModelAttribute("currentUser")
    public EmployeeDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        try {
            Long userId = securityService.getCurrentUserId();
            User user = userRepo.findById(userId).orElse(null);
            if (user != null && user.getEmployeeId() != null) {
                Employee emp = empRepo.findById(user.getEmployeeId()).orElse(null);
                if (emp != null) {
                    return employeeMapper.toDTO(emp, user.getRole().name(), user.getStatus().name());
                }
            }
        } catch (Exception e) {
            // Silently fail if session is invalid or user not found
        }
        return null;
    }
}
